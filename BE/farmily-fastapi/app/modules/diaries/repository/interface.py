"""
Diaries Repository Interface
"""
from abc import ABC, abstractmethod
from typing import List, Optional, Any
from datetime import datetime


class DiaryRepositoryInterface(ABC):
    """일기 리포지토리가 구현해야 할 인터페이스"""
    
    @abstractmethod
    async def get_all(self, owner_id: int, plant_id: Optional[int] = None) -> List[Any]:
        """사용자의 모든 일기 조회 (recorded_at 최신순)"""
        pass
    
    @abstractmethod
    async def get_by_id(self, diary_id: int, owner_id: int) -> Optional[Any]:
        """ID로 일기 조회 (소유권 검증 포함)"""
        pass
    
    @abstractmethod
    async def create(
        self,
        owner_id: int,
        content: str,
        recorded_at: datetime,
        image_url: Optional[str] = None,
        plant_id: Optional[int] = None
    ) -> Any:
        """새 일기 생성"""
        pass
    
    @abstractmethod
    async def update(
        self,
        diary_id: int,
        owner_id: int,
        content: Optional[str] = None,
        recorded_at: Optional[datetime] = None,
        image_url: Optional[str] = None
    ) -> Optional[Any]:
        """일기 수정 (부분 업데이트)"""
        pass
    
    @abstractmethod
    async def delete(self, diary_id: int, owner_id: int) -> bool:
        """일기 삭제 (성공 여부 반환)"""
        pass
