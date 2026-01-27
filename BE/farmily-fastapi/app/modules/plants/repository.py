"""
Plants - Repository (Mock Data)
DB 없이 더미 데이터를 제공하는 저장소
"""
from typing import Optional, List, Dict, Any

# 더미 데이터
# - 리스트 조회용: id, name, thumbnail
# - 상세 조회용: id, name, soil_moisture, temperature, humidity, image_url
_MOCK_PLANT_DB = [
    {
        "id": 1,
        "name": "상추",
        "thumbnail": "https://api.server.com/static/lettuce_t.jpg",
        "soil_moisture": 34,
        "temperature": 12,
        "humidity": 13,
        "image_url": "https://api.server.com/static/lettuce_full.jpg"
    },
    {
        "id": 2,
        "name": "바질",
        "thumbnail": None,
        "soil_moisture": 40,
        "temperature": 25,
        "humidity": 50,
        "image_url": None
    },
    {
        "id": 3,
        "name": "방울토마토",
        "thumbnail": "https://api.server.com/static/tomato_t.jpg",
        "soil_moisture": 50,
        "temperature": 22,
        "humidity": 60,
        "image_url": "https://api.server.com/static/tomato_full.jpg"
    },
    {
        "id": 32,
        "name": "상추 (예시 데이터)",
        "thumbnail": "https://api.server.com/static/lettuce_t.jpg",
        "soil_moisture": 34,
        "temperature": 12,
        "humidity": 13,
        "image_url": "https://api.server.com/static/lettuce_full.jpg"
    }
]

class PlantRepository:
    """식물 데이터 저장소 (Mock)"""
    
    async def get_all(self) -> List[Dict[str, Any]]:
        """모든 식물 데이터 반환"""
        return _MOCK_PLANT_DB
    
    async def get_by_id(self, plant_id: int) -> Optional[Dict[str, Any]]:
        """ID로 식물 조회"""
        for plant in _MOCK_PLANT_DB:
            if plant["id"] == plant_id:
                return plant
        return None

# 리포지토리 인스턴스
plant_repo = PlantRepository()
