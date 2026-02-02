"""
Light Control Service - State Machine for plant light automation.

States:
  MONITORING  - Collecting avg illuminance, waiting for low light
  LOW_LIGHT   - Low light detected, sending MOVE_TO command
  RETURNING   - Robot returning, waiting for arrival
  LIGHT_ON    - Robot arrived, light turned on, skip illuminance checks
"""
import asyncio
import math
from datetime import datetime, time, timedelta
from enum import Enum
from collections import deque
from typing import Optional
from loguru import logger
import httpx

from app.mqtt.config import (
    ACTIVE_TIME,
    ILLUMINANCE_CONFIG,
    MVP_USER_ID,
)


class ServiceState(str, Enum):
    MONITORING = "MONITORING"
    LOW_LIGHT = "LOW_LIGHT"
    RETURNING = "RETURNING"
    LIGHT_ON = "LIGHT_ON"


class LightControlService:
    """Singleton service for plant light control logic."""
    
    _instance: Optional["LightControlService"] = None
    
    def __new__(cls) -> "LightControlService":
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        
        self._initialized = True
        self._state = ServiceState.MONITORING
        self._illuminance_buffer: deque = deque(maxlen=1200)  # 20min at 1/sec
        self._light_on_until: Optional[datetime] = None
        
        # Plant config (loaded from API)
        self._plant_config: Optional[dict] = None
        self._config_loaded = False
        
        # Robot position
        self._robot_x: Optional[float] = None
        self._robot_y: Optional[float] = None
        
        # MQTT client reference (set later)
        self._mqtt_client = None
        
        logger.info("[LightControl] Service initialized")
    
    def set_mqtt_client(self, client) -> None:
        """Set MQTT client reference for publishing commands."""
        self._mqtt_client = client
    
    async def load_config(self) -> None:
        """Load plant config from internal API."""
        try:
            async with httpx.AsyncClient() as client:
                resp = await client.get(
                    "http://localhost:8081/api/v1/internal/plant-config",
                    timeout=10.0
                )
                if resp.status_code == 200:
                    self._plant_config = resp.json()
                    self._config_loaded = True
                    logger.info(f"[LightControl] Config loaded: {self._plant_config}")
                else:
                    logger.error(f"[LightControl] Failed to load config: {resp.status_code}")
        except Exception as e:
            logger.error(f"[LightControl] Config load error: {e}")
    
    def _is_active_time(self) -> bool:
        """Check if current time is within active hours."""
        now = datetime.now().time()
        start = time(ACTIVE_TIME["start_hour"], 0)
        end = time(ACTIVE_TIME["end_hour"], 0)
        return start <= now <= end
    
    def _get_avg_illuminance(self) -> float:
        """Calculate average illuminance from buffer."""
        if not self._illuminance_buffer:
            return 0.0
        return sum(self._illuminance_buffer) / len(self._illuminance_buffer)
    
    def _calculate_distance(self, x1: float, y1: float, x2: float, y2: float) -> float:
        """Calculate Euclidean distance between two points."""
        return math.sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2)
    
    async def process_illuminance(self, illuminance: float) -> None:
        """Process incoming illuminance data from sensor."""
        
        # Skip if light is currently on
        if self._state == ServiceState.LIGHT_ON:
            if self._light_on_until and datetime.now() > self._light_on_until:
                logger.info("[LightControl] Light duration ended, returning to MONITORING")
                self._state = ServiceState.MONITORING
                self._light_on_until = None
            else:
                logger.debug("[LightControl] Light ON, skipping illuminance check")
                return
        
        # Skip if returning (waiting for robot)
        if self._state == ServiceState.RETURNING:
            return
        
        # Add to buffer
        self._illuminance_buffer.append(illuminance)
        
        # Check only during active time
        if not self._is_active_time():
            return
        
        # Need config to check threshold
        if not self._config_loaded:
            await self.load_config()
            if not self._config_loaded:
                return
        
        avg_illuminance = self._get_avg_illuminance()
        threshold = self._plant_config.get("illuminance_target", 8000)
        
        # Check if low light condition
        if avg_illuminance < threshold and self._state == ServiceState.MONITORING:
            logger.info(
                f"[LightControl] LOW LIGHT DETECTED! "
                f"Avg: {avg_illuminance:.0f} < Threshold: {threshold}"
            )
            self._state = ServiceState.LOW_LIGHT
            await self._send_move_to_command()
    
    async def process_robot_position(self, x: float, y: float) -> None:
        """Process incoming robot position data."""
        self._robot_x = x
        self._robot_y = y
        
        # Only check arrival if we're waiting for robot
        if self._state != ServiceState.RETURNING:
            return
        
        if not self._config_loaded or not self._plant_config:
            return
        
        station_x = self._plant_config.get("station_x")
        station_y = self._plant_config.get("station_y")
        
        if station_x is None or station_y is None:
            logger.warning("[LightControl] Station position not configured")
            return
        
        distance = self._calculate_distance(x, y, station_x, station_y)
        proximity = ILLUMINANCE_CONFIG["station_proximity_m"]
        
        logger.debug(f"[LightControl] Robot distance to station: {distance:.3f}m")
        
        if distance <= proximity:
            logger.info(f"[LightControl] 🎯 ROBOT ARRIVED! Distance: {distance:.3f}m")
            await self._send_light_on_command()
    
    async def _send_move_to_command(self) -> None:
        """Send MOVE_TO command to Jetson bot."""
        if not self._mqtt_client or not self._plant_config:
            logger.error("[LightControl] Cannot send MOVE_TO: missing client or config")
            return
        
        station_x = self._plant_config.get("station_x", 0)
        station_y = self._plant_config.get("station_y", 0)
        
        from app.mqtt.publishers.command_publisher import publish_move_to
        await publish_move_to(self._mqtt_client, station_x, station_y)
        
        self._state = ServiceState.RETURNING
        logger.info(f"[LightControl] MOVE_TO sent, state → RETURNING")
    
    async def _send_light_on_command(self) -> None:
        """Send CONTROL_LIGHT ON command to station."""
        if not self._mqtt_client:
            logger.error("[LightControl] Cannot send CONTROL_LIGHT: missing client")
            return
        
        from app.mqtt.publishers.command_publisher import publish_control_light
        await publish_control_light(
            self._mqtt_client,
            state="ON",
            brightness=80,
            start_delay=60,
            duration=3600
        )
        
        self._state = ServiceState.LIGHT_ON
        self._light_on_until = datetime.now() + timedelta(seconds=3600)
        
        # Clear illuminance buffer to start fresh after light off
        self._illuminance_buffer.clear()
        
        logger.info(f"[LightControl] 💡 LIGHT ON sent, state → LIGHT_ON")
    
    @property
    def state(self) -> ServiceState:
        return self._state
    
    @property
    def avg_illuminance(self) -> float:
        return self._get_avg_illuminance()


# Singleton instance
light_control_service = LightControlService()
