from typing import List, Optional, Tuple
from sqlalchemy import select, and_
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import joinedload

from app.models.plant import Plant
from app.models.plant_log import PlantActivityCounts, PlantAchievement
from app.models.reference import RefAchievement, RefPlantSpecies, PlantActionType


class MypageRepository:
    """마이페이지 데이터 조회 레포지토리"""
    
    def __init__(self, db: AsyncSession):
        self.db = db
    
    async def get_active_plant(self, user_id: int) -> Optional[Plant]:
        """사용자의 활성 식물 조회 (species 정보 포함)"""
        stmt = (
            select(Plant)
            .options(joinedload(Plant.species))
            .where(
                and_(
                    Plant.users_id == user_id,
                    Plant.is_active == True
                )
            )
            .order_by(Plant.created_at.asc())
            .limit(1)
        )
        result = await self.db.execute(stmt)
        return result.scalar_one_or_none()
    
    async def get_activity_stats(self, plant_id: int) -> dict:
        """활동 통계 조회 (plant_activity_counts 테이블)"""
        stmt = select(PlantActivityCounts).where(
            PlantActivityCounts.plant_id == plant_id
        )
        result = await self.db.execute(stmt)
        counts = result.scalars().all()
        
        # 기본값으로 초기화
        stats = {
            "touch_count": 0,
            "water_count": 0,
            "talk_count": 0,
            "praise_count": 0,
            "diary_count": 0,
        }
        
        # DB에서 가져온 값으로 업데이트
        for count in counts:
            if count.activity_type == PlantActionType.TOUCH:
                stats["touch_count"] = count.total_count or 0
            elif count.activity_type == PlantActionType.WATER:
                stats["water_count"] = count.total_count or 0
            elif count.activity_type == PlantActionType.TALK:
                stats["talk_count"] = count.total_count or 0
            elif count.activity_type == PlantActionType.PRAISE:
                stats["praise_count"] = count.total_count or 0
            elif count.activity_type == PlantActionType.DIARY:
                stats["diary_count"] = count.total_count or 0
        
        return stats
    
    async def get_earned_achievements(self, plant_id: int) -> List[RefAchievement]:
        """달성한 도전과제 목록"""
        stmt = (
            select(RefAchievement)
            .join(PlantAchievement, PlantAchievement.ref_achievement_id == RefAchievement.id)
            .where(PlantAchievement.plant_id == plant_id)
            .order_by(RefAchievement.id.asc())
        )
        result = await self.db.execute(stmt)
        return list(result.scalars().all())
    
    async def get_unearned_achievements(self, plant_id: int) -> List[RefAchievement]:
        """미달성 도전과제 목록"""
        # 먼저 달성한 도전과제 ID 목록을 가져옴
        earned_stmt = (
            select(PlantAchievement.ref_achievement_id)
            .where(PlantAchievement.plant_id == plant_id)
        )
        earned_result = await self.db.execute(earned_stmt)
        earned_ids = [row[0] for row in earned_result.fetchall()]
        
        # 달성하지 않은 도전과제 조회
        if earned_ids:
            stmt = (
                select(RefAchievement)
                .where(RefAchievement.id.notin_(earned_ids))
                .order_by(RefAchievement.id.asc())
            )
        else:
            stmt = select(RefAchievement).order_by(RefAchievement.id.asc())
        
        result = await self.db.execute(stmt)
        return list(result.scalars().all())
    
    async def check_and_grant_achievement(
        self, 
        plant_id: int, 
        action_type: PlantActionType, 
        current_count: int
    ) -> Optional[PlantAchievement]:
        """
        도전과제 자동 부여: 현재 카운트에 해당하는 도전과제가 있고 
        아직 달성하지 않았다면 부여
        """
        # 해당 action_type과 required_count에 맞는 도전과제 찾기
        stmt = (
            select(RefAchievement)
            .where(
                and_(
                    RefAchievement.action_type == action_type,
                    RefAchievement.required_count == current_count
                )
            )
        )
        result = await self.db.execute(stmt)
        achievement = result.scalar_one_or_none()
        
        if not achievement:
            return None
        
        # 이미 달성했는지 확인
        check_stmt = (
            select(PlantAchievement)
            .where(
                and_(
                    PlantAchievement.plant_id == plant_id,
                    PlantAchievement.ref_achievement_id == achievement.id
                )
            )
        )
        check_result = await self.db.execute(check_stmt)
        if check_result.scalar_one_or_none():
            return None  # 이미 달성함
        
        # 새로운 도전과제 부여
        new_achievement = PlantAchievement(
            plant_id=plant_id,
            ref_achievement_id=achievement.id
        )
        self.db.add(new_achievement)
        await self.db.flush()
        
        return new_achievement
