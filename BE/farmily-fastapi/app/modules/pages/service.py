"""
Pages - 서비스 로직
"""
from typing import Optional
from app.modules.pages.schemas import MyPageResponse
from app.modules.pages.repository.interface import PageRepositoryInterface

class PageService:
    """페이지 데이터 집계 서비스"""
    
    def __init__(self, repository: PageRepositoryInterface):
        self.repository = repository
    
    async def get_mypage(self, user_id: int) -> Optional[MyPageResponse]:
        """마이페이지 정보 조회"""
        data = await self.repository.get_mypage_info(user_id)
        
        if not data:
            return None
            
        # Pydantic 모델로 변환 및 검증
        return MyPageResponse(**data)
