from fastapi import APIRouter, HTTPException, Depends
from loguru import logger

from app.schemas.control import JetsonMoveRequest
from app.mqtt.client import mqtt_client
from app.mqtt.publishers.command_publisher import publish_move_to

router = APIRouter()

@router.post("/jetson/move", summary="Move Jetson Robot")
async def move_jetson(request: JetsonMoveRequest):
    """
    Send a command to move the Jetson robot to specific coordinates.
    """
    if not mqtt_client.is_connected:
        raise HTTPException(status_code=503, detail="MQTT broker not connected")
    
    try:
        await publish_move_to(
            mqtt_client,
            x=request.x,
            y=request.y,
            theta=request.theta,
            device_id="jetson_bot"
        )
        return {"status": "success", "message": f"Command sent: Move to ({request.x}, {request.y}, {request.theta})"}
    except Exception as e:
        logger.error(f"Failed to send move command: {e}")
        raise HTTPException(status_code=500, detail=str(e))
