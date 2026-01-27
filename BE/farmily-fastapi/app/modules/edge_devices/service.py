"""
Edge Devices - 서비스 로직
"""
from app.core.logger import logger
from app.modules.edge_devices.schemas import DeviceCommandRequest

class EdgeDeviceService:
    """
    [Step 3] 장비 관리 및 제어 로직
    - 들어온 명령을 검증하고 MQTT 발행
    """
    
    async def send_command(self, device_id: str, request: DeviceCommandRequest):
        """장비 명령 전송"""
        # TODO: infra.mqtt_client를 호출하여 메시지 발행
        logger.info(f"Command requested for {device_id}")
        pass

# 서비스 인스턴스
edge_device_service = EdgeDeviceService()
