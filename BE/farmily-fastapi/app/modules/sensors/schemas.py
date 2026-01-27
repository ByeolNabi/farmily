"""
Sensors - 데이터 검증 스키마 (Pydantic)
"""
from pydantic import BaseModel

# TODO: [Step 4] 센서 데이터 스키마를 정의하세요.

class SensorDataCreateRequest(BaseModel):
    """센서 데이터 저장 요청 예시"""
    pass
