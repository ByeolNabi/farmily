"""
Diaries - API 라우터
"""
from typing import Optional, Dict
from datetime import datetime

from fastapi import APIRouter, Depends, status, UploadFile, File, Form
from fastapi.responses import JSONResponse, Response

from app.modules.diaries.service import DiaryService
from app.modules.diaries.schemas import (
    DiaryListResponse,
    DiaryDetailResponse,
    DiaryCreateResponse,
    DiaryUpdateResponse,
    ErrorResponse
)
from app.modules.diaries.dependencies import get_diary_repository, get_current_user
from app.modules.diaries.repository.interface import DiaryRepositoryInterface
from app.common.files import save_upload_file

router = APIRouter()


# === Dependencies ===
def get_service(
    repo: DiaryRepositoryInterface = Depends(get_diary_repository)
) -> DiaryService:
    return DiaryService(repository=repo)


# === Endpoints ===

@router.get("", response_model=DiaryListResponse, responses={
    401: {"model": ErrorResponse, "description": "인증 실패"}
})
async def get_diaries(
    plant_id: Optional[int] = None,
    service: DiaryService = Depends(get_service),
    current_user: Dict = Depends(get_current_user)
):
    """
    전체 일기 목록 조회
    사용자의 모든 일기를 recorded_at 최신순으로 반환합니다.
    """

    diaries = await service.get_diary_list(
        owner_id=current_user["user_id"],
        plant_id=plant_id
    )
    return DiaryListResponse(
        total_count=len(diaries),
        diaries=diaries
    )


@router.post("", response_model=DiaryCreateResponse, status_code=status.HTTP_200_OK, responses={
    400: {"model": ErrorResponse, "description": "필수 필드 누락"},
    413: {"model": ErrorResponse, "description": "파일 용량 초과"}
})
async def create_diary(
    plant_id: int = Form(..., description="연관된 식물 ID"),
    content: str = Form(..., description="일기 본문 내용"),
    recorded_at: datetime = Form(..., description="기록 시점 (ISO 8601)"),
    image: Optional[UploadFile] = File(None, description="식물 사진 파일"),
    service: DiaryService = Depends(get_service),
    current_user: Dict = Depends(get_current_user)
):
    """
    일기 생성 (multipart/form-data)
    content와 recorded_at은 필수입니다.
    """
    # 이미지 저장 (10MB 제한은 Nginx나 FastAPI 미들웨어에서 처리 권장)
    image_url = None
    if image:
        image_url = await save_upload_file(image)
    
    result = await service.create_diary(
        owner_id=current_user["user_id"],
        content=content,
        recorded_at=recorded_at,
        image_url=image_url,
        plant_id=plant_id
    )
    return result


@router.get("/{diary_id}", response_model=DiaryDetailResponse, responses={
    400: {"model": ErrorResponse, "description": "잘못된 ID 형식"},
    401: {"model": ErrorResponse, "description": "인증 실패"},
    404: {"model": ErrorResponse, "description": "일기를 찾을 수 없음"}
})
async def get_diary_detail(
    diary_id: int,
    service: DiaryService = Depends(get_service),
    current_user: Dict = Depends(get_current_user)
):
    """
    일기 상세 조회
    """
    diary = await service.get_diary_detail(
        diary_id=diary_id,
        owner_id=current_user["user_id"]
    )
    
    if not diary:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "DIARY_NOT_FOUND",
                "message": "해당 일기를 찾을 수 없습니다."
            }
        )
    
    return diary


@router.patch("/{diary_id}", response_model=DiaryUpdateResponse, responses={
    400: {"model": ErrorResponse, "description": "잘못된 요청"},
    401: {"model": ErrorResponse, "description": "인증 실패"},
    404: {"model": ErrorResponse, "description": "일기를 찾을 수 없음"}
})
async def update_diary(
    diary_id: int,
    content: Optional[str] = Form(None, description="수정할 본문 내용"),
    recorded_at: Optional[datetime] = Form(None, description="수정할 기록 시점"),
    image: Optional[UploadFile] = File(None, description="변경할 새 이미지"),
    service: DiaryService = Depends(get_service),
    current_user: Dict = Depends(get_current_user)
):
    """
    일기 수정 (부분 업데이트)
    """
    # 새 이미지가 있으면 저장
    image_url = None
    if image:
        image_url = await save_upload_file(image)
        # TODO: 기존 이미지 삭제 로직 필요 (선택 사항)
    
    updated = await service.update_diary(
        diary_id=diary_id,
        owner_id=current_user["user_id"],
        content=content,
        recorded_at=recorded_at,
        image_url=image_url
    )
    
    if not updated:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "DIARY_NOT_FOUND",
                "message": "수정할 일기를 찾을 수 없습니다."
            }
        )
    
    return updated


@router.delete("/{diary_id}", status_code=status.HTTP_204_NO_CONTENT, responses={
    401: {"model": ErrorResponse, "description": "삭제 권한 없음"},
    404: {"model": ErrorResponse, "description": "일기를 찾을 수 없음"}
})
async def delete_diary(
    diary_id: int,
    service: DiaryService = Depends(get_service),
    current_user: Dict = Depends(get_current_user)
):
    """
    일기 삭제
    성공 시 204 No Content를 반환합니다.
    """
    deleted = await service.delete_diary(
        diary_id=diary_id,
        owner_id=current_user["user_id"]
    )
    
    if not deleted:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "DIARY_NOT_FOUND",
                "message": "삭제할 일기를 찾을 수 없습니다."
            }
        )
    
    return Response(status_code=status.HTTP_204_NO_CONTENT)
