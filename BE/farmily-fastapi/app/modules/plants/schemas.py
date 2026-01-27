"""
Plants - 데이터 검증 스키마 (Pydantic)
"""
from typing import Optional, List
from pydantic import BaseModel, Field

# === Response Schemas ===

class PlantSummary(BaseModel):
    """식물 목록 조회용 요약 정보"""
    id: int = Field(..., description="식물 고유 ID")
    name: str = Field(..., description="식물 이름 (국명)")
    thumbnail: Optional[str] = Field(None, description="리스트용 작은 이미지 URL")

class PlantListResponse(BaseModel):
    """식물 전체 목록 응답"""
    total_count: int = Field(..., description="전체 식물 수")
    plants: List[PlantSummary] = Field(..., description="식물 요약 정보 리스트")

class PlantDetailResponse(BaseModel):
    """식물 상세 정보 응답"""
    id: int = Field(..., description="식물 고유 ID")
    name: str = Field(..., description="식물 이름")
    soil_moisture: int = Field(..., description="적정 토양 습도 (%)")
    temperature: int = Field(..., description="적정 온도 (°C)")
    humidity: int = Field(..., description="적정 공기 습도 (%)")
    image_url: Optional[str] = Field(None, description="상세 페이지용 고화질 이미지 URL")

class ErrorResponse(BaseModel):
    """에러 응답 공통 포맷"""
    error_code: str
    message: str
