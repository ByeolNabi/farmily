from typing import List, Optional
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.reference import RefPlantSpecies

class RefPlantRepository:
    def __init__(self, session: AsyncSession):
        self.session = session

    async def get_all(self) -> List[RefPlantSpecies]:
        stmt = select(RefPlantSpecies)
        result = await self.session.execute(stmt)
        return result.scalars().all()

    async def get_by_id(self, plant_id: int) -> Optional[RefPlantSpecies]:
        stmt = select(RefPlantSpecies).where(RefPlantSpecies.id == plant_id)
        result = await self.session.execute(stmt)
        return result.scalar_one_or_none()
        
    async def count_all(self) -> int:
        stmt = select(func.count()).select_from(RefPlantSpecies)
        result = await self.session.execute(stmt)
        return result.scalar() or 0
