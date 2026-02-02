"""
Generic handler for 'farmily/devices/+' events and commands.
"""
import httpx
from loguru import logger
from pydantic import ValidationError

from app.mqtt.schemas import MQTTHeader, EventPayload
from app.mqtt.config import Topics


async def handle_device_event(topic: str, data: dict) -> None:
    """Handle generic device messages (mostly events).
    
    Args:
        topic: farmily/devices/{device_id}/event
        data: Raw message dictionary
    """
    try:
        header = MQTTHeader(**data.get("header", {}))
        msg_type = header.type
        device_id = header.device_id
        
        # Only process events here
        if msg_type == "event":
            payload_data = data.get("payload", {})
            if "params" not in payload_data:
                payload_data["params"] = {}
                
            payload = EventPayload(**payload_data)
            event_name = payload.event
            
            # 1. 로그 출력 (구분)
            if event_name == "WATER":
                logger.info(f"💦 [WATER EVENT] Device: {device_id} sent WATER event")
                await _send_points_api(action="WATER")
                
            elif event_name == "TOUCH":
                logger.info(f"👆 [TOUCH EVENT] Device: {device_id} sent TOUCH event")
                await _send_points_api(action="TOUCH")
            
            else:
                logger.info(f"✨ [EVENT RECEIVED] Device: {device_id} | Event: {event_name}")
                
        else:
            logger.debug(f"[Device] Ignored {msg_type} on device topic")

    except ValidationError as e:
        logger.error(f"[Device] Invalid format: {e}")


async def _send_points_api(action: str) -> None:
    """Send points update to API."""
    from app.core.jwt_utils import service_auth
    
    url = "https://i14d101.p.ssafy.io/api/plants/2/points"
    body = {"action": action}
    
    # Get JWT auth headers
    headers = service_auth.get_auth_headers()
    
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(url, json=body, headers=headers)
            
            if response.status_code == 200:
                logger.info(f"✅ API Call Success: {action} -> {response.json()}")
            else:
                logger.error(f"❌ API Call Failed: {response.status_code} - {response.text}")
                
    except Exception as e:
        logger.error(f"❌ API Call Error: {e}")



def register_device_event_handler(mqtt_client) -> None:
    mqtt_client.subscribe(Topics.DEVICE_ALL_SUB, handle_device_event)
    logger.info(f"Registered device event handler: {Topics.DEVICE_ALL_SUB}")
