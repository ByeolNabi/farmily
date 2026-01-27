"""
Timelapse Repository Interface
"""
from abc import ABC, abstractmethod
from typing import List, Optional, Any
from datetime import datetime


class TimelapseRepositoryInterface(ABC):
    """타임랩스 리포지토리가 구현해야 할 인터페이스"""
    
    @abstractmethod
    async def get_all(self, plant_id: Optional[int] = None) -> List[Any]:
        """모든 타임랩스 사진 조회 (taken_at 오름차순)"""
        pass
    
    @abstractmethod
    async def create(
        self,
        image_url: str,
        taken_at: datetime,
        plant_id: Optional[int] = None
    ) -> Any:
        """새 타임랩스 사진 추가"""
        pass
    
    @abstractmethod
    async def delete(self, photo_id: int) -> bool:
        """타임랩스 사진 삭제 (성공 여부 반환)"""
        pass
