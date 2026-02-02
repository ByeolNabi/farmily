"""
Sensor Aggregation Service - Collects sensor data and posts averages to API.

Collects sensor readings over a configurable interval (default 10 minutes),
calculates averages, and POSTs to external API for plant_sensor_log storage.
"""
import asyncio
from datetime import datetime
from typing import Optional, List
from loguru import logger
import httpx

from app.mqtt.config import SENSOR_AGGREGATION_CONFIG, MVP_USER_ID


class SensorAggregationService:
    """Singleton service for sensor data aggregation and API posting."""
    
    _instance: Optional["SensorAggregationService"] = None
    
    def __new__(cls) -> "SensorAggregationService":
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        
        self._initialized = True
        
        # Data buffer: list of sensor_dict entries
        self._sensor_buffer: List[dict] = []
        self._last_aggregation_time: datetime = datetime.now()
        
        # Plant config (loaded from DB, same as LightControlService)
        self._plant_id: Optional[int] = None
        self._config_loaded = False
        
        # MQTT client reference (optional, for future use)
        self._mqtt_client = None
        
        logger.info("[SensorAggregation] Service initialized")
    
    def set_mqtt_client(self, client) -> None:
        """Set MQTT client reference."""
        self._mqtt_client = client
    
    async def load_config(self) -> None:
        """Load plant config from DB to get plant_id."""
        from sqlalchemy import select
        from app.core.database import AsyncSessionLocal
        from app.models.plant import Plant
        
        try:
            async with AsyncSessionLocal() as db:
                stmt = (
                    select(Plant.id)
                    .where(Plant.users_id == MVP_USER_ID)
                    .where(Plant.is_active == True)
                    .order_by(Plant.created_at.desc())
                    .limit(1)
                )
                
                result = await db.execute(stmt)
                row = result.first()
                
                if row:
                    self._plant_id = row.id
                    self._config_loaded = True
                    logger.info(f"[SensorAggregation] Config loaded: plant_id={self._plant_id}")
                else:
                    logger.warning(f"[SensorAggregation] No active plant found for user {MVP_USER_ID}")
                    
        except Exception as e:
            logger.error(f"[SensorAggregation] Config load error: {e}")
    
    async def add_sensor_data(self, sensor_dict: dict) -> None:
        """Add sensor data to buffer and check if aggregation is needed.
        
        Args:
            sensor_dict: Dictionary with temperature, humidity, illuminance, soil_moisture
        """
        # Add to buffer
        self._sensor_buffer.append(sensor_dict)
        
        # Check if it's time to aggregate
        elapsed = (datetime.now() - self._last_aggregation_time).total_seconds()
        interval = SENSOR_AGGREGATION_CONFIG["interval_sec"]
        
        if elapsed >= interval:
            await self._aggregate_and_post()
    
    async def _aggregate_and_post(self) -> None:
        """Calculate averages and post to API."""
        if not self._sensor_buffer:
            logger.debug("[SensorAggregation] No data to aggregate")
            return
        
        # Load config if not loaded
        if not self._config_loaded:
            await self.load_config()
            if not self._config_loaded:
                logger.warning("[SensorAggregation] Cannot post: config not loaded")
                return
        
        # Calculate averages
        count = len(self._sensor_buffer)
        averages = {
            "temperature": sum(d.get("temperature", 0) for d in self._sensor_buffer) / count,
            "humidity": sum(d.get("humidity", 0) for d in self._sensor_buffer) / count,
            "illuminance": sum(d.get("illuminance", 0) for d in self._sensor_buffer) / count,
            "soil_moisture": sum(d.get("soil_moisture", 0) for d in self._sensor_buffer) / count,
        }
        
        # Round to 1 decimal place
        averages = {k: round(v, 1) for k, v in averages.items()}
        
        logger.info(
            f"[SensorAggregation] Aggregated {count} samples: "
            f"Temp={averages['temperature']}°C "
            f"Hum={averages['humidity']}% "
            f"Light={averages['illuminance']}lux "
            f"Soil={averages['soil_moisture']}%"
        )
        
        # Post to API
        await self._post_to_api(averages)
        
        # Reset buffer and timer
        self._sensor_buffer.clear()
        self._last_aggregation_time = datetime.now()
    
    async def _post_to_api(self, data: dict) -> None:
        """Post aggregated data to external API.
        
        Args:
            data: Dictionary with temperature, humidity, illuminance, soil_moisture
        """
        from app.core.jwt_utils import service_auth
        
        base_url = SENSOR_AGGREGATION_CONFIG["api_base_url"]
        path_template = SENSOR_AGGREGATION_CONFIG["api_path_template"]
        url = f"{base_url}{path_template.format(plant_id=self._plant_id)}"
        
        # Get JWT auth headers
        headers = service_auth.get_auth_headers()
        
        try:
            async with httpx.AsyncClient(timeout=10.0) as client:
                response = await client.post(url, json=data, headers=headers)
                
                if response.status_code in (200, 201):
                    logger.info(f"[SensorAggregation] ✅ Posted to API: {url}")
                else:
                    logger.warning(
                        f"[SensorAggregation] API response {response.status_code}: {response.text}"
                    )
        except httpx.RequestError as e:
            logger.error(f"[SensorAggregation] API request failed: {e}")
        except Exception as e:
            logger.error(f"[SensorAggregation] Unexpected error: {e}")
    
    @property
    def buffer_size(self) -> int:
        """Current number of samples in buffer."""
        return len(self._sensor_buffer)
    
    @property
    def plant_id(self) -> Optional[int]:
        """Currently configured plant ID."""
        return self._plant_id


# Singleton instance
sensor_aggregation_service = SensorAggregationService()
