"""
Timelapse - Mock Repository implementation
"""
from typing import List, Optional, Dict, Any
from datetime import datetime, timezone, timedelta
from app.modules.timelapse.repository.interface import TimelapseRepositoryInterface

# In-memory 더미 데이터 (taken_at 기준 오름차순)
_MOCK_TIMELAPSE_DB: List[Dict[str, Any]] = [
    {
        "id": 1,
        "plant_id": 1,
        "image_url": "https://api.server.com/static/timelapse/tl_001.jpg",
        "taken_at": datetime(2026, 1, 1, 9, 0, 0, tzinfo=timezone.utc),
    },
    {
        "id": 2,
        "plant_id": 1,
        "image_url": "https://api.server.com/static/timelapse/tl_002.jpg",
        "taken_at": datetime(2026, 1, 2, 9, 0, 0, tzinfo=timezone.utc),
    },
    {
        "id": 3,
        "plant_id": 1,
        "image_url": "https://api.server.com/static/timelapse/tl_003.jpg",
        "taken_at": datetime(2026, 1, 3, 9, 0, 0, tzinfo=timezone.utc),
    },
    {
        "id": 4,
        "plant_id": 1,
        "image_url": "https://api.server.com/static/timelapse/tl_004.jpg",
        "taken_at": datetime(2026, 1, 4, 9, 0, 0, tzinfo=timezone.utc),
    },
    {
        "id": 5,
        "plant_id": 1,
        "image_url": "https://api.server.com/static/timelapse/tl_005.jpg",
        "taken_at": datetime(2026, 1, 5, 9, 0, 0, tzinfo=timezone.utc),
    },
]

_NEXT_ID = 6


class MockTimelapseRepository(TimelapseRepositoryInterface):
    """메모리 상의 더미 데이터를 사용하는 리포지토리"""
    
    async def get_all(self, plant_id: Optional[int] = None) -> List[Dict[str, Any]]:
        """모든 타임랩스 사진 조회 (taken_at 오름차순)"""
        if plant_id:
            photos = [p for p in _MOCK_TIMELAPSE_DB if p["plant_id"] == plant_id]
        else:
            photos = _MOCK_TIMELAPSE_DB.copy()
        
        return sorted(photos, key=lambda x: x["taken_at"])
    
    async def create(
        self,
        image_url: str,
        taken_at: datetime,
        plant_id: Optional[int] = None
    ) -> Dict[str, Any]:
        """새 타임랩스 사진 추가"""
        global _NEXT_ID
        
        new_photo = {
            "id": _NEXT_ID,
            "plant_id": plant_id,
            "image_url": image_url,
            "taken_at": taken_at,
        }
        _MOCK_TIMELAPSE_DB.append(new_photo)
        _NEXT_ID += 1
        
        return new_photo
    
    async def delete(self, photo_id: int) -> bool:
        """타임랩스 사진 삭제"""
        global _MOCK_TIMELAPSE_DB
        
        for i, photo in enumerate(_MOCK_TIMELAPSE_DB):
            if photo["id"] == photo_id:
                _MOCK_TIMELAPSE_DB.pop(i)
                return True
        return False
