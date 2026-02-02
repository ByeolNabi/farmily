"""
Handler for Jetson lidar position telemetry.
Subscribes to: farmily/jetson/lidar/pos
"""
from loguru import logger
from pydantic import ValidationError

from app.mqtt.schemas import MQTTHeader, JetsonPosPayload
from app.mqtt.config import Topics
from app.mqtt.services.light_control_service import light_control_service


async def handle_jetson_position(topic: str, data: dict) -> None:
    """Handle Jetson robot position telemetry.
    
    Args:
        topic: farmily/jetson/lidar/pos
        data: Raw message dictionary
    """
    try:
        header = MQTTHeader(**data.get("header", {}))
        payload = JetsonPosPayload(**data.get("payload", {}))
        
        logger.debug(
            f"[Jetson] Position update: "
            f"x={payload.x:.2f}, y={payload.y:.2f}, theta={payload.theta:.1f}"
        )
        
        # Forward to light control service
        await light_control_service.process_robot_position(payload.x, payload.y)
        
    except ValidationError as e:
        logger.error(f"[Jetson] Invalid position format: {e}")
    except Exception as e:
        logger.error(f"[Jetson] Error processing position: {e}")


def register_jetson_handler(mqtt_client) -> None:
    """Register Jetson position handler with MQTT client."""
    mqtt_client.subscribe(Topics.JETSON_POS, handle_jetson_position)
    logger.info(f"Registered Jetson position handler: {Topics.JETSON_POS}")
