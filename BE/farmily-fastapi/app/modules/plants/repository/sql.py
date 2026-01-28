"""
Plants - SQL Repository implementation
"""
from typing import List, Optional, Any
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.modules.plants.repository.interface import PlantRepositoryInterface
# from app.modules.plants.models import Plant  # 모델이 구현되면 주석 해제

class SQLPlantRepository(PlantRepositoryInterface):
    """실제 DB를 사용하는 리포지토리"""
    
    def __init__(self, session: AsyncSession):
        self.session = session
        
    async def get_all(self) -> List[Any]:
        # TODO: 실제 DB 쿼리 구현
        # result = await self.session.execute(select(Plant))
        # return result.scalars().all()
        return []

    async def get_by_id(self, plant_id: int) -> Optional[Any]:
        # TODO: 실제 DB 쿼리 구현
        # result = await self.session.execute(select(Plant).where(Plant.id == plant_id))
        # return result.scalar_one_or_none()
        return None
