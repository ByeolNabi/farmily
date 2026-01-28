"""
Plants - API 라우터
"""
from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse

from app.modules.plants.service import PlantService
from app.modules.plants.schemas import PlantListResponse, PlantDetailResponse, ErrorResponse
from app.modules.plants.dependencies import get_plant_repository
from app.modules.plants.repository.interface import PlantRepositoryInterface

router = APIRouter()

# === Dependencies ===
def get_service(repo: PlantRepositoryInterface = Depends(get_plant_repository)) -> PlantService:
    return PlantService(repository=repo)

@router.get("", response_model=PlantListResponse)
async def get_plants(
    service: PlantService = Depends(get_service)
):
    """
    식물 전체 목록 조회
    """
    plants = await service.get_plant_list()
    return PlantListResponse(
        total_count=len(plants),
        plants=plants
    )

@router.get("/{plant_id}", response_model=PlantDetailResponse, responses={
    404: {"model": ErrorResponse, "description": "식물을 찾을 수 없음"}
})
async def get_plant_detail(
    plant_id: int,
    service: PlantService = Depends(get_service)
):
    """
    식물 상세 정보 조회
    """
    plant = await service.get_plant_detail(plant_id)
    
    if not plant:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "PLANT_NOT_FOUND",
                "message": "해당 식물을 찾을 수 없습니다."
            }
        )
    
    return plant
