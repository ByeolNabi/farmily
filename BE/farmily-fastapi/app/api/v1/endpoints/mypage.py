from typing import Annotated

from fastapi import APIRouter, Depends

from app.api.deps import CurrentUser, DbSession
from app.repositories.mypage_repository import MypageRepository
from app.services.mypage_service import MypageService
from app.schemas.mypage import MypageResponse

router = APIRouter()


async def get_service(db: DbSession) -> MypageService:
    """Dependency Injection helper"""
    repository = MypageRepository(db)
    return MypageService(repository)


Service = Annotated[MypageService, Depends(get_service)]


@router.get(
    "",
    response_model=MypageResponse,
    summary="마이페이지 정보 조회",
    description="안드로이드 앱의 마이페이지에 보여지는 모든 정보를 가져옵니다.",
    responses={
        200: {"description": "마이페이지 정보를 불러왔습니다."},
        401: {"description": "로그인 정보가 유효하지 않습니다."},
        404: {"description": "활성화된 식물이 없습니다."},
        500: {"description": "서버 통신 중 오류가 발생했습니다."},
    }
)
async def get_mypage(
    current_user: CurrentUser,
    service: Service,
) -> MypageResponse:
    """
    마이페이지 정보를 반환합니다.
    
    - **plant_info**: 식물 및 캐릭터 기본 정보
    - **activity_stats**: 행위별 누적 활동 통계
    - **achievements**: 달성/미달성 뱃지 리스트
    """
    return await service.get_mypage_info(current_user.id)
