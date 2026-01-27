"""
Sensors - 서비스 로직
"""
from app.core.logger import logger
from app.modules.sensors.schemas import SensorDataCreateRequest

class SensorService:
    """
    [Step 4] 데이터 수집 및 통계 로직
    """
    
    async def process_data(self, data: SensorDataCreateRequest):
        # TODO: 데이터 가공 및 저장
        logger.info("Processing sensor data")
        pass

# 서비스 인스턴스
sensor_service = SensorService()
