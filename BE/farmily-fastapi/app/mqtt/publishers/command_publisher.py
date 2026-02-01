"""
Command publisher for sending commands to Unity display.
"""
import uuid
from datetime import datetime
from loguru import logger

from app.mqtt.schemas import MQTTHeader, CommandPayload, MQTTMessage
from app.mqtt.config import Topics


async def publish_command(
    mqtt_client,
    cmd: str,
    params: dict,
    device_id: str = "farmily-server"
) -> None:
    """Publish a command message.
    
    Args:
        mqtt_client: MQTTClient instance
        cmd: Command name (e.g., "UPDATE_WEATHER", "UPDATE_CONDITION")
        params: Command parameters
        device_id: Source device ID
    """
    header = MQTTHeader(
        msg_id=str(uuid.uuid4()),
        type="command",
        device_id=device_id,
        timestamp=datetime.now()
    )
    
    payload = CommandPayload(cmd=cmd, params=params)
    
    message = MQTTMessage[CommandPayload](header=header, payload=payload)
    
    await mqtt_client.publish(
        Topics.UNITY_COMMAND,
        message.model_dump()
    )
    
    logger.info(f"[Command] Published {cmd} with params: {params}")


async def publish_weather_update(mqtt_client, weather: str) -> None:
    """Publish weather update command to Unity.
    
    Args:
        mqtt_client: MQTTClient instance
        weather: Weather state ("SUNNY", "RAINY", "CLOUDY")
    """
    await publish_command(
        mqtt_client,
        cmd="UPDATE_WEATHER",
        params={"weather": weather}
    )


async def publish_condition_update(mqtt_client, condition: str) -> None:
    """Publish plant condition update command to Unity.
    
    Args:
        mqtt_client: MQTTClient instance
        condition: Plant condition ("HEALTHY", "SICK", "THIRSTY")
    """
    await publish_command(
        mqtt_client,
        cmd="UPDATE_CONDITION",
        params={"condition": condition}
    )


async def publish_event(
    mqtt_client,
    event: str,
    params: dict = None,
    device_id: str = "farmily-server"
) -> None:
    """Publish an event message.
    
    Args:
        mqtt_client: MQTTClient instance
        event: Event name (e.g., "WATER_DETECTED", "TOUCH_DETECTED")
        params: Event parameters
        device_id: Source device ID
    """
    header = MQTTHeader(
        msg_id=str(uuid.uuid4()),
        type="event",
        device_id=device_id,
        timestamp=datetime.now()
    )
    
    if params is None:
        params = {}
        
    payload = {"event": event, "params": params}
    
    message = {"header": header.model_dump(), "payload": payload}
    
    await mqtt_client.publish(Topics.SENSOR_EVENT, message)
    
    logger.info(f"[Event] Published {event} with params: {params}")
