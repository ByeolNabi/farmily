"""
Pages - API 라우터
"""
from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse

from app.modules.pages.service import PageService
from app.modules.pages.schemas import MyPageResponse, ErrorResponse
from app.modules.pages.dependencies import get_page_repository
from app.modules.pages.repository.interface import PageRepositoryInterface

router = APIRouter()

# === Dependencies ===
def get_service(repo: PageRepositoryInterface = Depends(get_page_repository)) -> PageService:
    return PageService(repository=repo)

@router.get("/mypage", response_model=MyPageResponse, responses={
    500: {"model": ErrorResponse, "description": "서버 오류"}
})
async def get_mypage_info(
    service: PageService = Depends(get_service)
):
    """
    마이페이지 정보 조회
    
    - **plant_info**: 식물 및 캐릭터 기본 정보
    - **activity_stats**: 행위별 누적 활동 통계
    - **achievements**: 뱃지 달성 현황
    """
    # 현재 인증이 없으므로 user_id=1로 하드코딩 (Mocking을 위해)
    user_id = 1
    
    mypage_data = await service.get_mypage(user_id)
    
    if not mypage_data:
        # Mock 데이터가 비어있을 리는 없지만 예외 처리
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={
                "error_code": "SERVER_ERROR",
                "message": "데이터를 불러올 수 없습니다."
            }
        )
    
    return mypage_data
