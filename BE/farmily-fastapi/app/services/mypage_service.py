from datetime import date
from typing import Optional

from fastapi import HTTPException, status

from app.repositories.mypage_repository import MypageRepository
from app.schemas.mypage import (
    MypageResponse,
    PlantInfoSchema,
    ActivityStatsSchema,
    BadgeSchema,
    AchievementsSchema,
)


class MypageService:
    """마이페이지 비즈니스 로직"""
    
    def __init__(self, repository: MypageRepository):
        self.repository = repository
    
    async def get_mypage_info(self, user_id: int) -> MypageResponse:
        """마이페이지 전체 정보 조합"""
        
        # 1. 활성 식물 조회
        plant = await self.repository.get_active_plant(user_id)
        
        if not plant:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="활성화된 식물이 없습니다."
            )
        
        # 2. 활동 통계 조회
        activity_stats = await self.repository.get_activity_stats(plant.id)
        
        # 3. 도전과제 조회
        earned_achievements = await self.repository.get_earned_achievements(plant.id)
        unearned_achievements = await self.repository.get_unearned_achievements(plant.id)
        
        # 4. days_met 계산 (오늘 기준 함께한 일수)
        today = date.today()
        if plant.started_at:
            start_date = plant.started_at.date()
            days_met = (today - start_date).days + 1  # 시작일 포함
        else:
            start_date = today
            days_met = 1
        
        # 5. Response 조합
        plant_info = PlantInfoSchema(
            nickname=plant.nickname or "이름 없음",
            species=plant.species.name if plant.species else "알 수 없음",
            character_url=plant.profile_image_url,
            start_date=start_date.strftime("%Y-%m-%d"),
            days_met=days_met,
            love_temperature=float(plant.love_temperature)
        )
        
        activity_stats_schema = ActivityStatsSchema(
            petting_count=activity_stats["petting_count"],
            watering_count=activity_stats["watering_count"],
            talking_count=activity_stats["talking_count"],
            praising_count=activity_stats["praising_count"],
            diary_count=activity_stats["diary_count"],
        )
        
        earned_badges = [
            BadgeSchema(
                id=ach.id,
                name=ach.name or "",
                icon_url=ach.icon_url
            )
            for ach in earned_achievements
        ]
        
        unearned_badges = [
            BadgeSchema(
                id=ach.id,
                name=ach.name or "",
                icon_url=ach.icon_url
            )
            for ach in unearned_achievements
        ]
        
        achievements = AchievementsSchema(
            earned_badges=earned_badges,
            unearned_badges=unearned_badges
        )
        
        return MypageResponse(
            plant_info=plant_info,
            activity_stats=activity_stats_schema,
            achievements=achievements
        )
