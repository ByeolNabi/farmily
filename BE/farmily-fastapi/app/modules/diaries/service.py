"""
Diaries - 서비스 로직
"""
from typing import Optional, List
from datetime import datetime

from app.modules.diaries.schemas import (
    DiarySummary,
    DiaryDetailResponse,
    DiaryCreateResponse,
    DiaryUpdateResponse
)
from app.modules.diaries.repository.interface import DiaryRepositoryInterface


class DiaryService:
    """일기 관리 서비스"""
    
    def __init__(self, repository: DiaryRepositoryInterface):
        self.repository = repository
    
    async def get_diary_list(self, owner_id: int) -> List[DiarySummary]:
        """사용자의 모든 일기 조회"""
        raw_data = await self.repository.get_all(owner_id)
        
        return [
            DiarySummary(
                id=item["id"],
                content=item["content"],
                image_url=item["image_url"],
                recorded_at=item["recorded_at"],
                created_at=item["created_at"]
            )
            for item in raw_data
        ]
    
    async def get_diary_detail(self, diary_id: int, owner_id: int) -> Optional[DiaryDetailResponse]:
        """일기 상세 조회"""
        data = await self.repository.get_by_id(diary_id, owner_id)
        
        if not data:
            return None
        
        return DiaryDetailResponse(
            id=data["id"],
            content=data["content"],
            image_url=data["image_url"],
            recorded_at=data["recorded_at"],
            created_at=data["created_at"]
        )
    
    async def create_diary(
        self,
        owner_id: int,
        content: str,
        recorded_at: datetime,
        image_url: Optional[str] = None
    ) -> DiaryCreateResponse:
        """새 일기 생성"""
        created = await self.repository.create(
            owner_id=owner_id,
            content=content,
            recorded_at=recorded_at,
            image_url=image_url
        )
        
        return DiaryCreateResponse(
            diary_id=created["id"],
            image_url=created["image_url"],
            recorded_at=created["recorded_at"],
            created_at=created["created_at"]
        )
    
    async def update_diary(
        self,
        diary_id: int,
        owner_id: int,
        content: Optional[str] = None,
        recorded_at: Optional[datetime] = None,
        image_url: Optional[str] = None
    ) -> Optional[DiaryUpdateResponse]:
        """일기 수정"""
        updated = await self.repository.update(
            diary_id=diary_id,
            owner_id=owner_id,
            content=content,
            recorded_at=recorded_at,
            image_url=image_url
        )
        
        if not updated:
            return None
        
        return DiaryUpdateResponse(
            id=updated["id"],
            content=updated["content"],
            image_url=updated["image_url"],
            recorded_at=updated["recorded_at"],
            updated_at=updated["updated_at"]
        )
    
    async def delete_diary(self, diary_id: int, owner_id: int) -> bool:
        """일기 삭제"""
        return await self.repository.delete(diary_id, owner_id)
