from typing import List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, delete, func, desc
from app.models.plant_log import PlantDiary
from app.models.plant import Plant

class PlantDiaryRepository:
    def __init__(self, db: AsyncSession):
        self.db = db

    async def create(self, diary: PlantDiary) -> PlantDiary:
        self.db.add(diary)
        await self.db.commit()
        await self.db.refresh(diary)
        return diary

    async def get_by_id(self, diary_id: int) -> Optional[PlantDiary]:
        stmt = select(PlantDiary).where(PlantDiary.id == diary_id)
        result = await self.db.execute(stmt)
        return result.scalar_one_or_none()

    async def get_all(self, skip: int = 0, limit: int = 10, plant_id: Optional[int] = None, user_id: Optional[int] = None) -> List[PlantDiary]:
        stmt = select(PlantDiary)
        
        # If user_id is provided, we need to join with Plant to filter by owner
        if user_id:
            stmt = stmt.join(Plant).where(Plant.users_id == user_id)
            
        if plant_id:
            stmt = stmt.where(PlantDiary.plant_id == plant_id)
        
        # Sort by happened_at DESC
        stmt = stmt.order_by(desc(PlantDiary.happened_at))
        stmt = stmt.offset(skip).limit(limit)
        
        result = await self.db.execute(stmt)
        return result.scalars().all()

    async def count(self, plant_id: Optional[int] = None, user_id: Optional[int] = None) -> int:
        stmt = select(func.count()).select_from(PlantDiary)
        
        if user_id:
            stmt = stmt.join(Plant).where(Plant.users_id == user_id)

        if plant_id:
            stmt = stmt.where(PlantDiary.plant_id == plant_id)
            
        result = await self.db.execute(stmt)
        return result.scalar() or 0

    async def delete(self, diary_id: int):
        stmt = delete(PlantDiary).where(PlantDiary.id == diary_id)
        await self.db.execute(stmt)
        await self.db.commit()

    async def get_plant_owner_id(self, plant_id: int) -> Optional[int]:
         stmt = select(Plant.users_id).where(Plant.id == plant_id)
         result = await self.db.execute(stmt)
         return result.scalar_one_or_none()
         
    async def get_diary_owner_id(self, diary_id: int) -> Optional[int]:
        # Join Plant to get user_id of the diary's plant
        stmt = select(Plant.users_id).join(PlantDiary).where(PlantDiary.id == diary_id)
        result = await self.db.execute(stmt)
        return result.scalar_one_or_none()
