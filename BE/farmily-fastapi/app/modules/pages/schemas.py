"""
Pages - 데이터 검증 스키마 (Pydantic)
"""
from typing import List, Optional
from pydantic import BaseModel, Field

# === Sub Models ===

class PlantInfo(BaseModel):
    """식물 및 캐릭터 기본 정보"""
    nickname: str = Field(..., description="식물 애칭", examples=["밤티두리"])
    species: str = Field(..., description="식물 종 이름", examples=["몬스테라"])
    character_url: str = Field(..., description="캐릭터 이미지 URL")
    start_date: str = Field(..., description="시작 날짜 (YYYY-MM-DD)", examples=["2025-10-20"])
    days_met: int = Field(..., description="함께한 날짜 수", examples=[97])
    bond_warmth: float = Field(..., description="친밀함 온도 (0.0~100.0)", examples=[43.5])

class ActivityStats(BaseModel):
    """행위별 누적 활동 통계"""
    petting_count: int = Field(..., description="쓰다듬기 횟수")
    watering_count: int = Field(..., description="물주기 횟수")
    talking_count: int = Field(..., description="대화하기 횟수")
    praising_count: int = Field(..., description="칭찬하기 횟수")
    diary_count: int = Field(..., description="일기 기록 횟수")

class Badge(BaseModel):
    """뱃지 정보"""
    id: int = Field(..., description="뱃지 고유 ID")
    name: str = Field(..., description="뱃지 이름")
    icon: str = Field(..., description="앱 로컬 내 리소스 파일명")

class Achievements(BaseModel):
    """뱃지 달성 시스템 데이터"""
    earned_badges: List[Badge] = Field(..., description="달성한 뱃지 리스트")
    unearned_badges: List[Badge] = Field(..., description="미달성 뱃지 리스트")

# === Response Model ===

class MyPageResponse(BaseModel):
    """마이페이지 전체 정보 응답"""
    plant_info: PlantInfo
    activity_stats: ActivityStats
    achievements: Achievements

class ErrorResponse(BaseModel):
    """에러 응답 공통 포맷"""
    error_code: str
    message: str
