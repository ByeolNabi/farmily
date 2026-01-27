"""
Plants - 서비스 로직
"""
from typing import Optional, List
from app.core.exceptions import FarmilyException
from app.modules.plants.repository import plant_repo
from app.modules.plants.schemas import PlantSummary, PlantDetailResponse

class PlantService:
    """식물 정보 관리 서비스"""
    
    async def get_plant_list(self) -> List[PlantSummary]:
        """식물 전체 목록 조회"""
        raw_data = await plant_repo.get_all()
        
        # 스키마에 맞게 변환
        return [
            PlantSummary(
                id=item["id"],
                name=item["name"],
                thumbnail=item["thumbnail"]
            )
            for item in raw_data
        ]
    
    async def get_plant_detail(self, plant_id: int) -> Optional[PlantDetailResponse]:
        """식물 상세 정보 조회"""
        data = await plant_repo.get_by_id(plant_id)
        
        if not data:
            return None
        
        return PlantDetailResponse(
            id=data["id"],
            name=data["name"],
            soil_moisture=data["soil_moisture"],
            temperature=data["temperature"],
            humidity=data["humidity"],
            image_url=data["image_url"]
        )

# 서비스 인스턴스
plant_service = PlantService()
