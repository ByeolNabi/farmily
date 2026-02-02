from typing import List, Optional
from sqlalchemy import select, desc, asc
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.plant_log import PlantTimelapse


class TimelapseRepository:
    """타임랩스 데이터 접근 리포지토리"""
    
    def __init__(self, db: AsyncSession):
        self.db = db
    
    async def get_timelapses(self, plant_id: int) -> List[PlantTimelapse]:
        """특정 식물의 타임랩스 사진 목록을 촬영일 오름차순으로 조회 (과거 -> 현재)"""
        stmt = (
            select(PlantTimelapse)
            .where(PlantTimelapse.plant_id == plant_id)
            .order_by(PlantTimelapse.created_at.asc())
        )
        result = await self.db.execute(stmt)
        return list(result.scalars().all())
    
    async def get_timelapse(self, photo_id: int) -> Optional[PlantTimelapse]:
        """ID로 타임랩스 사진 단건 조회"""
        stmt = select(PlantTimelapse).where(PlantTimelapse.id == photo_id)
        result = await self.db.execute(stmt)
        return result.scalar_one_or_none()
        
    async def create_timelapse(self, timelapse: PlantTimelapse) -> PlantTimelapse:
        """타임랩스 사진 정보 DB 저장"""
        self.db.add(timelapse)
        await self.db.flush()
        await self.db.commit()  # <-- Commit added to persist data
        await self.db.refresh(timelapse)
        return timelapse
    
    async def delete_timelapse(self, timelapse: PlantTimelapse) -> None:
        """타임랩스 사진 정보 DB 삭제"""
        await self.db.delete(timelapse)
        await self.db.commit()  # <-- Commit added to persist deletion
