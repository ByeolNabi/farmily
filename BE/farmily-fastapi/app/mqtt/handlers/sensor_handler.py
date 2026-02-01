"""
Sensor data handler for raspi sensor telemetry.
Handles messages from 'farmily/raspi/sensor/all' topic.
"""
from loguru import logger
from pydantic import ValidationError

from app.mqtt.schemas import MQTTHeader, SensorPayload, MQTTMessage
from app.mqtt.config import check_sensor_status, Topics


async def handle_sensor_data(topic: str, data: dict) -> None:
    """Handle incoming sensor telemetry data.
    
    Args:
        topic: MQTT topic (should be Topics.SENSOR_ALL)
        data: Raw message dictionary
    """
    try:
        # Parse and validate message
        header = MQTTHeader(**data.get("header", {}))
        payload = SensorPayload(**data.get("payload", {}))
        
        message = MQTTMessage[SensorPayload](header=header, payload=payload)
        
        logger.info(
            f"[Sensor] device={header.device_id} "
            f"temp={payload.temperature}°C "
            f"humidity={payload.humidity}% "
            f"light={payload.illuminance}lux "
            f"soil={payload.soil_moisture}%"
        )
        
        # Check against thresholds
        sensor_dict = {
            "temperature": payload.temperature,
            "humidity": payload.humidity,
            "illuminance": payload.illuminance,
            "soil_moisture": payload.soil_moisture,
        }
        status = check_sensor_status(sensor_dict)
        
        # Log any abnormal values
        for key, value in status.items():
            if value != "normal":
                logger.warning(f"[Sensor] {key} is {value}: {sensor_dict[key]}")
        
        # TODO: Store sensor data to database
        # TODO: Trigger events based on status changes
        
    except ValidationError as e:
        logger.error(f"[Sensor] Invalid message format: {e}")
    except Exception as e:
        logger.error(f"[Sensor] Error processing message: {e}")


def register_sensor_handlers(mqtt_client) -> None:
    """Register sensor handlers with MQTT client.
    
    Args:
        mqtt_client: MQTTClient instance
    """
    mqtt_client.subscribe(Topics.SENSOR_ALL, handle_sensor_data)
    logger.info(f"Registered sensor handler for topic: {Topics.SENSOR_ALL}")
