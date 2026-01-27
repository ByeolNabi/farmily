"""
Plants - API 라우터
"""
from fastapi import APIRouter, status
from fastapi.responses import JSONResponse

from app.modules.plants.service import plant_service
from app.modules.plants.schemas import PlantListResponse, PlantDetailResponse, ErrorResponse

router = APIRouter()

@router.get("", response_model=PlantListResponse)
async def get_plants():
    """file
    식물 전체 목록 조회
    
    - **total_count**: 전체 식물 수
    - **plants**: 식물 요약 정보 리스트
    """
    plants = await plant_service.get_plant_list()
    return PlantListResponse(
        total_count=len(plants),
        plants=plants
    )

@router.get("/{plant_id}", response_model=PlantDetailResponse, responses={
    400: {"model": ErrorResponse, "description": "잘못된 요청"},
    404: {"model": ErrorResponse, "description": "식물을 찾을 수 없음"},
    500: {"model": ErrorResponse, "description": "서버 오류"}
})
async def get_plant_detail(plant_id: int):
    """
    식물 상세 정보 조회
    
    - **id**: 식물 고유 ID
    - **name**: 식물 이름
    - **soil_moisture**: 적정 토양 습도
    - **temperature**: 적정 온도
    - **humidity**: 적정 공기 습도
    - **image_url**: 상세 이미지
    """
    plant = await plant_service.get_plant_detail(plant_id)
    
    if not plant:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "PLANT_NOT_FOUND",
                "message": "해당 식물을 찾을 수 없습니다."
            }
        )
    
    return plant
