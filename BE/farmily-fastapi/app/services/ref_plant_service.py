from typing import List

from fastapi import HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.repositories.ref_plant_repository import RefPlantRepository
from app.schemas.ref_plant import RefPlantSpeciesDetailResponse, RefPlantSpeciesSummaryResponse

class RefPlantService:
    def __init__(self, session: AsyncSession):
        self.repository = RefPlantRepository(session)

    async def get_ref_plants(self) -> dict:
        total_count = await self.repository.count_all()
        plants = await self.repository.get_all()
        
        # Schema 변환은 Router 또는 Pydantic ConfigDict(from_attributes=True)에 의해 자동 처리될 수 있지만
        # 명시적으로 리턴 구조를 맞춤
        return {
            "total_count": total_count,
            "plants": plants
        }

    async def get_ref_plant_detail(self, plant_id: int) -> RefPlantSpeciesDetailResponse:
        plant = await self.repository.get_by_id(plant_id)
        if not plant:
            raise HTTPException(status_code=404, detail="해당 식물을 찾을 수 없습니다.")
            
        return plant
