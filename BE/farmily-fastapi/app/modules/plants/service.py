"""
Plants - 서비스 로직
"""
from typing import Optional, List
from app.modules.plants.schemas import PlantSummary, PlantDetailResponse
from app.modules.plants.repository.interface import PlantRepositoryInterface

class PlantService:
    """식물 정보 관리 서비스"""
    
    def __init__(self, repository: PlantRepositoryInterface):
        self.repository = repository
    
    async def get_plant_list(self) -> List[PlantSummary]:
        """식물 전체 목록 조회"""
        raw_data = await self.repository.get_all()
        
        # 스키마에 맞게 변환
        return [
            PlantSummary(
                id=item["id"],
                name=item["name"],
                thumbnail=item["thumbnail"] if isinstance(item, dict) else item.thumbnail
            )
            for item in raw_data
        ]
    
    async def get_plant_detail(self, plant_id: int) -> Optional[PlantDetailResponse]:
        """식물 상세 정보 조회"""
        data = await self.repository.get_by_id(plant_id)
        
        if not data:
            return None
            
        # Mock(dict) vs SQL(object) 처리
        if isinstance(data, dict):
            return PlantDetailResponse(
                id=data["id"],
                name=data["name"],
                soil_moisture=data["soil_moisture"],
                temperature=data["temperature"],
                humidity=data["humidity"],
                image_url=data["image_url"]
            )
        else:
            # SQL 모델 객체인 경우 (Pydantic의 from_attributes=True 활용 가능하지만 명시적 변환)
            return PlantDetailResponse(
                id=data.id,
                name=data.name,
                soil_moisture=data.soil_moisture,
                temperature=data.temperature,
                humidity=data.humidity,
                image_url=data.image_url
            )
