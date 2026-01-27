"""
Sensors - API 라우터
"""
from fastapi import APIRouter
from app.modules.sensors.service import sensor_service

router = APIRouter()

@router.get("/stats")
async def get_stats():
    """
    [Step 4] 통계 조회 API
    """
    return {"status": "ok", "message": "Not implemented yet"}
