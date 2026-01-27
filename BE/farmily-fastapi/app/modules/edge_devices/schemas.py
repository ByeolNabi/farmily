"""
Edge Devices - 데이터 검증 스키마 (Pydantic)
"""
from pydantic import BaseModel

# TODO: [Step 3] 필요한 요청/응답 스키마를 정의하세요.

class DeviceCreateRequest(BaseModel):
    """장비 등록 요청 예시"""
    # device_id: str
    # name: str
    pass

class DeviceCommandRequest(BaseModel):
    """장비 명령 요청 예시"""
    # command: str
    pass
