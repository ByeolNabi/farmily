"""
Diaries - 데이터 검증 스키마 (Pydantic)
"""
from typing import Optional, List
from datetime import datetime
from pydantic import BaseModel, Field


# === Request Schemas ===

class DiaryCreateRequest(BaseModel):
    """일기 생성 요청"""
    content: str = Field(..., min_length=1, description="일기 본문 내용")
    recorded_at: datetime = Field(..., description="기록 시점 (ISO 8601)")


class DiaryUpdateRequest(BaseModel):
    """일기 수정 요청 (Partial Update)"""
    content: Optional[str] = Field(None, min_length=1, description="수정할 본문 내용")
    recorded_at: Optional[datetime] = Field(None, description="수정할 기록 시점")


# === Response Schemas ===

class DiarySummary(BaseModel):
    """일기 목록 조회용 요약 정보"""
    id: int = Field(..., description="일기 고유 PK")
    content: str = Field(..., description="일기 본문 내용")
    image_url: Optional[str] = Field(None, description="이미지 경로")
    recorded_at: datetime = Field(..., description="사용자가 기록한 시간")
    created_at: datetime = Field(..., description="서버 저장 시간")


class DiaryListResponse(BaseModel):
    """일기 전체 목록 응답"""
    total_count: int = Field(..., description="전체 일기 개수")
    diaries: List[DiarySummary] = Field(..., description="일기 정보 리스트")


class DiaryDetailResponse(BaseModel):
    """일기 상세 조회 응답"""
    id: int = Field(..., description="일기 고유 PK")
    content: str = Field(..., description="일기 본문 내용")
    image_url: Optional[str] = Field(None, description="고화질 이미지 URL")
    recorded_at: datetime = Field(..., description="사용자가 기록한 시간")
    created_at: datetime = Field(..., description="서버 저장 시간")


class DiaryCreateResponse(BaseModel):
    """일기 생성 응답"""
    diary_id: int = Field(..., description="생성된 일기 고유 ID")
    image_url: Optional[str] = Field(None, description="저장된 이미지 URL")
    recorded_at: datetime = Field(..., description="기록 시점")
    created_at: datetime = Field(..., description="서버 저장 시간")


class DiaryUpdateResponse(BaseModel):
    """일기 수정 응답"""
    id: int = Field(..., description="수정된 일기 고유 PK")
    content: str = Field(..., description="최종 수정된 본문")
    image_url: Optional[str] = Field(None, description="최종 이미지 경로")
    recorded_at: datetime = Field(..., description="최종 수정된 기록 시점")
    updated_at: datetime = Field(..., description="서버 수정 완료 시간")


# === Error Response ===

class ErrorResponse(BaseModel):
    """에러 응답 공통 포맷"""
    error_code: str = Field(..., description="에러 코드")
    message: str = Field(..., description="에러 메시지")
