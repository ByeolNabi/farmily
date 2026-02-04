from datetime import date
from typing import List, Optional
from pydantic import BaseModel, Field


class PlantInfoSchema(BaseModel):
    """식물 및 캐릭터 기본 정보"""
    nickname: str = Field(..., description="사용자가 설정한 식물 애칭")
    species: str = Field(..., description="식물 종 이름")
    character_url: Optional[str] = Field(None, description="캐릭터 이미지 URL")
    start_date: str = Field(..., description="처음 키우기 시작한 날짜 (YYYY-MM-DD)")
    days_met: int = Field(..., description="오늘 기준 함께한 날짜 수")
    love_temperature: float = Field(..., description="친밀함 온도 (0.0~100.0)")


class ActivityStatsSchema(BaseModel):
    """행위별 누적 활동 통계"""
    touch_count: int = Field(0, description="쓰다듬기 누적 횟수")
    water_count: int = Field(0, description="물주기 누적 횟수")
    talk_count: int = Field(0, description="대화하기 누적 횟수")
    praise_count: int = Field(0, description="칭찬하기 누적 횟수")
    diary_count: int = Field(0, description="사진 일기 기록 누적 횟수")


class BadgeSchema(BaseModel):
    """뱃지 정보"""
    id: int = Field(..., description="뱃지 고유 ID")
    name: str = Field(..., description="뱃지 이름")
    icon_url: Optional[str] = Field(None, description="앱 로컬 내 리소스 파일명")


class AchievementsSchema(BaseModel):
    """뱃지 달성 시스템 데이터"""
    earned_badges: List[BadgeSchema] = Field(default_factory=list, description="달성한 뱃지 리스트")
    unearned_badges: List[BadgeSchema] = Field(default_factory=list, description="미달성 뱃지 리스트")


class MypageResponse(BaseModel):
    """마이페이지 전체 응답"""
    plant_info: PlantInfoSchema
    activity_stats: ActivityStatsSchema
    achievements: AchievementsSchema

    class Config:
        from_attributes = True
