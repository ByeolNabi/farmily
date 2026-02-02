"""
Specific handler for 'farmily/raspi/sensor/all' telemetry topic.
"""
from loguru import logger
from pydantic import ValidationError

from app.mqtt.schemas import MQTTHeader, SensorPayload
from app.mqtt.config import Topics, check_sensor_status
from app.mqtt.services.light_control_service import light_control_service


async def handle_sensor_telemetry(topic: str, data: dict) -> None:
    """Handle fixed telemetry topic messages.
    
    Args:
        topic: farmily/raspi/sensor/all
        data: Raw message dictionary
    """
    try:
        # Validate Header & Payload
        header = MQTTHeader(**data.get("header", {}))
        payload = SensorPayload(**data.get("payload", {}))
        
        device_id = header.device_id
        
        logger.debug(
            f"[Telemetry] {device_id} | "
            f"Temp={payload.temperature}°C "
            f"Hum={payload.humidity}% "
            f"Light={payload.illuminance}lux "
            f"Soil={payload.soil_moisture}%"
        )
        
        # Check thresholds
        sensor_dict = {
            "temperature": payload.temperature,
            "humidity": payload.humidity,
            "illuminance": payload.illuminance,
            "soil_moisture": payload.soil_moisture,
        }
        status = check_sensor_status(sensor_dict)
        
        for key, value in status.items():
            if value != "normal":
                logger.debug(f"[Telemetry] {device_id} {key} is {value}: {sensor_dict[key]}")
        
        # Forward illuminance to light control service
        await light_control_service.process_illuminance(payload.illuminance)
                
    except ValidationError as e:
        logger.error(f"[Telemetry] Invalid format: {e}")
    except Exception as e:
        logger.error(f"[Telemetry] Error: {e}")


def register_sensor_handler(mqtt_client) -> None:
    mqtt_client.subscribe(Topics.SENSOR_ALL, handle_sensor_telemetry)
    logger.info(f"Registered telemetry handler: {Topics.SENSOR_ALL}")
