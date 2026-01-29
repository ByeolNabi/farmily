from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.database import get_db
from app.services.ref_plant_service import RefPlantService
from app.schemas.ref_plant import RefPlantSpeciesListResponse, RefPlantSpeciesDetailResponse

router = APIRouter()

@router.get("", response_model=RefPlantSpeciesListResponse)
async def get_ref_plants(db: AsyncSession = Depends(get_db)):
    """
    식물 전체 목록 조회 (default)
    """
    service = RefPlantService(db)
    return await service.get_ref_plants()


@router.get("/{plant_id}", response_model=RefPlantSpeciesDetailResponse)
async def get_ref_plant_detail(plant_id: int, db: AsyncSession = Depends(get_db)):
    """
    id에 해당하는 식물의 상세 데이터
    """
    service = RefPlantService(db)
    return await service.get_ref_plant_detail(plant_id)
