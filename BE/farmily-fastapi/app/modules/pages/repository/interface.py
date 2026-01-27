"""
Pages Repository Interface
"""
from abc import ABC, abstractmethod
from typing import Optional, Dict, Any

class PageRepositoryInterface(ABC):
    """모든 페이지 리포지토리가 구현해야 할 인터페이스"""
    
    @abstractmethod
    async def get_mypage_info(self, user_id: int) -> Optional[Dict[str, Any]]:
        """사용자 마이페이지 정보 조회"""
        pass
