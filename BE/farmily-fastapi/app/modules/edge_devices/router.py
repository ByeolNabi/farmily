"""
Edge Devices - API 라우터
"""
from fastapi import APIRouter
from app.modules.edge_devices.schemas import DeviceCommandRequest
from app.modules.edge_devices.service import edge_device_service

router = APIRouter()

@router.post("/{device_id}/command")
async def send_command(device_id: str, request: DeviceCommandRequest):
    """
    [Step 3] POST /devices/{id}/command 엔드포인트
    """
    await edge_device_service.send_command(device_id, request)
    return {"status": "ok", "message": "Not implemented yet"}
