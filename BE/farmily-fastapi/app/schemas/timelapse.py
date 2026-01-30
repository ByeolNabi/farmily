from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel, ConfigDict, Field


class TimelapsePhotoSchema(BaseModel):
    """타임랩스 개별 사진 정보"""
    photo_id: int = Field(validation_alias="id")
    image_url: str
    created_at: datetime
    
    model_config = ConfigDict(from_attributes=True)


class TimelapseListResponse(BaseModel):
    """타임랩스 목록 응답"""
    total_frames: int
    photos: List[TimelapsePhotoSchema]


class TimelapseCreateResponse(BaseModel):
    """타임랩스 생성 응답"""
    photo_id: int = Field(validation_alias="id")
    image_url: str
    created_at: datetime
    
    model_config = ConfigDict(from_attributes=True)
