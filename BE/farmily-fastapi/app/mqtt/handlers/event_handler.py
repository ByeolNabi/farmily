"""
Event handler for device events (e.g. WATER_DETECTED, TOUCH_DETECTED).
Handles messages from 'farmily/raspi/event' topic.
"""
from loguru import logger
from pydantic import ValidationError

from app.mqtt.schemas import MQTTHeader, EventPayload, MQTTMessage
from app.mqtt.config import Topics


async def handle_device_event(topic: str, data: dict) -> None:
    """Handle incoming device events.
    
    Args:
        topic: MQTT topic (should be Topics.SENSOR_EVENT)
        data: Raw message dictionary
    """
    try:
        # Parse and validate message
        header = MQTTHeader(**data.get("header", {}))
        payload_data = data.get("payload", {})
        
        # params가 없는 경우 빈 딕셔너리로 처리 (스키마 변경 사항 반영)
        if "params" not in payload_data:
            payload_data["params"] = {}
            
        payload = EventPayload(**payload_data)
        
        # Log event
        event_name = payload.event
        device_id = header.device_id
        params = payload.params
        
        logger.info(f"[Event] Received {event_name} from {device_id}")
        
        if event_name == "WATER_DETECTED":
            # TODO: Handle water detected logic (e.g. update DB, notify Unity)
            logger.info(f"💦 Water detected! Params: {params}")
            
        elif event_name == "TOUCH_DETECTED":
            # TODO: Handle touch logic
            logger.info(f"👆 Touch detected! Params: {params}")
            
        else:
            logger.warning(f"Unknown event type: {event_name}")
            
    except ValidationError as e:
        logger.error(f"[Event] Invalid message format: {e}")
    except Exception as e:
        logger.error(f"[Event] Error processing message: {e}")


def register_event_handlers(mqtt_client) -> None:
    """Register event handlers with MQTT client.
    
    Args:
        mqtt_client: MQTTClient instance
    """
    mqtt_client.subscribe(Topics.SENSOR_EVENT, handle_device_event)
    logger.info(f"Registered event handler for topic: {Topics.SENSOR_EVENT}")
