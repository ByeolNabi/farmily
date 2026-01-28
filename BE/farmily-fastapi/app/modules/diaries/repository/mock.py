"""
Diaries - Mock Repository implementation
"""
from typing import List, Optional, Dict, Any
from datetime import datetime, timezone
from app.modules.diaries.repository.interface import DiaryRepositoryInterface

# In-memory 더미 데이터
_MOCK_DIARY_DB: List[Dict[str, Any]] = [
    {
        "id": 1,
        "owner_id": 1,
        "content": "드디어 새 순이 나왔어요! 🌱 매일 물을 주며 기다린 보람이 있네요.",
        "image_url": "https://api.server.com/static/diary_1.jpg",
        "recorded_at": datetime(2026, 1, 23, 14, 30, 0, tzinfo=timezone.utc),
        "created_at": datetime(2026, 1, 23, 14, 35, 0, tzinfo=timezone.utc),
        "updated_at": None
    },
    {
        "id": 2,
        "owner_id": 1,
        "content": "이틀 전 비 올 때 창문을 열어뒀더니 잎이 더 싱싱해진 것 같아요.",
        "image_url": None,
        "recorded_at": datetime(2026, 1, 25, 10, 0, 0, tzinfo=timezone.utc),
        "created_at": datetime(2026, 1, 25, 10, 5, 0, tzinfo=timezone.utc),
        "updated_at": None
    },
]

_NEXT_ID = 3


class MockDiaryRepository(DiaryRepositoryInterface):
    """메모리 상의 더미 데이터를 사용하는 리포지토리"""
    
    async def get_all(self, owner_id: int, plant_id: Optional[int] = None) -> List[Dict[str, Any]]:
        """사용자의 모든 일기 조회 (recorded_at 최신순)"""
        user_diaries = [d for d in _MOCK_DIARY_DB if d["owner_id"] == owner_id]
        return sorted(user_diaries, key=lambda x: x["recorded_at"], reverse=True)
    
    async def get_by_id(self, diary_id: int, owner_id: int) -> Optional[Dict[str, Any]]:
        """ID로 일기 조회 (소유권 검증 포함)"""
        for diary in _MOCK_DIARY_DB:
            if diary["id"] == diary_id and diary["owner_id"] == owner_id:
                return diary
        return None
    
    async def create(
        self,
        owner_id: int,
        content: str,
        recorded_at: datetime,
        image_url: Optional[str] = None,
        plant_id: Optional[int] = None
    ) -> Dict[str, Any]:
        """새 일기 생성"""
        global _NEXT_ID
        
        now = datetime.now(timezone.utc)
        new_diary = {
            "id": _NEXT_ID,
            "owner_id": owner_id,
            "content": content,
            "image_url": image_url,
            "recorded_at": recorded_at,
            "created_at": now,
            "updated_at": None
        }
        _MOCK_DIARY_DB.append(new_diary)
        _NEXT_ID += 1
        
        return new_diary
    
    async def update(
        self,
        diary_id: int,
        owner_id: int,
        content: Optional[str] = None,
        recorded_at: Optional[datetime] = None,
        image_url: Optional[str] = None
    ) -> Optional[Dict[str, Any]]:
        """일기 수정 (부분 업데이트)"""
        for diary in _MOCK_DIARY_DB:
            if diary["id"] == diary_id and diary["owner_id"] == owner_id:
                if content is not None:
                    diary["content"] = content
                if recorded_at is not None:
                    diary["recorded_at"] = recorded_at
                if image_url is not None:
                    diary["image_url"] = image_url
                diary["updated_at"] = datetime.now(timezone.utc)
                return diary
        return None
    
    async def delete(self, diary_id: int, owner_id: int) -> bool:
        """일기 삭제"""
        global _MOCK_DIARY_DB
        
        for i, diary in enumerate(_MOCK_DIARY_DB):
            if diary["id"] == diary_id and diary["owner_id"] == owner_id:
                _MOCK_DIARY_DB.pop(i)
                return True
        return False
