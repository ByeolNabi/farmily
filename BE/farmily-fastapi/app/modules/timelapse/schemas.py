"""
Timelapse - 데이터 검증 스키마 (Pydantic)
"""
from typing import Optional, List
from datetime import datetime
from pydantic import BaseModel, Field


# === Response Schemas ===

class TimelapsePhoto(BaseModel):
    """타임랩스 개별 사진"""
    frame_no: int = Field(..., ge=0, description="재생 순서 (0부터 시작)")
    image_url: str = Field(..., description="타임랩스용 이미지 경로")
    taken_at: datetime = Field(..., description="실제 촬영 시간 (ISO 8601)")


class TimelapseListResponse(BaseModel):
    """타임랩스 사진 목록 응답"""
    total_frames: int = Field(..., description="전체 타임랩스 사진 개수")
    photos: List[TimelapsePhoto] = Field(..., description="타임랩스 사진 리스트")


class TimelapseUploadResponse(BaseModel):
    """타임랩스 사진 업로드 응답"""
    photo_id: int = Field(..., description="생성된 사진 고유 ID")
    frame_no: int = Field(..., description="재생 순서")
    image_url: str = Field(..., description="저장된 이미지 URL")
    taken_at: datetime = Field(..., description="촬영 시간")


# === Error Response ===

class ErrorResponse(BaseModel):
    """에러 응답 공통 포맷"""
    error_code: str = Field(..., description="에러 코드")
    message: str = Field(..., description="에러 메시지")
