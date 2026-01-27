"""
Plants - Mock Repository implementation
"""
from typing import List, Optional, Dict, Any
from app.modules.plants.repository.interface import PlantRepositoryInterface

# 더미 데이터
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

class MockPlantRepository(PlantRepositoryInterface):
    """메모리 상의 더미 데이터를 사용하는 리포지토리"""
    
    async def get_all(self) -> List[Dict[str, Any]]:
        return _MOCK_PLANT_DB
    
    async def get_by_id(self, plant_id: int) -> Optional[Dict[str, Any]]:
        for plant in _MOCK_PLANT_DB:
            if plant["id"] == plant_id:
                return plant
        return None
