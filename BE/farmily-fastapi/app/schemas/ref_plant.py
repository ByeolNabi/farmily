from pydantic import BaseModel, Field, ConfigDict, field_validator
from datetime import datetime
from typing import Optional, Any

# Range Object Helper
class RangeObject(BaseModel):
    min: int
    max: int

# Base: 공통 필드
class RefPlantSpeciesBase(BaseModel):
    name: str = Field(..., description="식물 이름 (국명)")
    image_url: Optional[str] = Field(None, description="이미지 경로")

# Response (List Item)
class RefPlantSpeciesSummaryResponse(RefPlantSpeciesBase):
    id: int
    
    model_config = ConfigDict(from_attributes=True)

# Response (Detail)
class RefPlantSpeciesDetailResponse(RefPlantSpeciesBase):
    id: int
    temp_target: int
    temp_range: RangeObject
    humid_target: int
    humid_range: RangeObject
    soil_target: int
    soil_range: RangeObject
    light_intensity: Optional[int]
    created_at: datetime
    
    model_config = ConfigDict(from_attributes=True)

    @field_validator("temp_range", "humid_range", "soil_range", mode="before")
    @classmethod
    def parse_range(cls, v: Any) -> dict:
        """
        SQLAlchemy/Postgres Range 객체 또는 문자열/딕셔너리를
        { "min": x, "max": y } 형태로 변환합니다.
        """
        if v is None:
            return {"min": 0, "max": 0}
            
        # 1. 이미 dict인 경우
        if isinstance(v, dict):
            return v
        
        # 2. SQLAlchemy Range 객체인 경우 (lower, upper 속성 존재)
        if hasattr(v, "lower") and hasattr(v, "upper"):
            return {
                "min": v.lower if v.lower is not None else 0,
                # upper가 비어있으면(무한대) 임시로 100 처리하거나 적절히 핸들링 필요.
                # 여기서는 보통 식물 생육 범위가 정해져 있으므로 값이 있다고 가정.
                "max": v.upper if v.upper is not None else 0 
                # Postgres Range는 [lower, upper) 즉 상한 미포함이 기본이지만
                # 유저 요구사항 {min, max}는 보통 inclusive로 해석됨.
                # 하지만 raw DB 값 변환이 우선이므로 값 그대로 매핑.
            }
            
        return {"min": 0, "max": 0}

# List Wrapper
class RefPlantSpeciesListResponse(BaseModel):
    total_count: int
    plants: list[RefPlantSpeciesSummaryResponse]
