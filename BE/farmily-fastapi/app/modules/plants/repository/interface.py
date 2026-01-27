"""
Plants Repository Interface
"""
from abc import ABC, abstractmethod
from typing import List, Optional, Any

class PlantRepositoryInterface(ABC):
    """모든 식물 리포지토리가 구현해야 할 인터페이스"""
    
    @abstractmethod
    async def get_all(self) -> List[Any]:
        """모든 식물 데이터 반환"""
        pass

    @abstractmethod
    async def get_by_id(self, plant_id: int) -> Optional[Any]:
        """ID로 식물 조회"""
        pass
