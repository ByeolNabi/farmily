"""
Timelapse - API 라우터
"""
from typing import Optional
from datetime import datetime

from fastapi import APIRouter, Depends, status, UploadFile, File, Form
from fastapi.responses import JSONResponse, Response

from app.modules.timelapse.service import TimelapseService
from app.modules.timelapse.schemas import (
    TimelapseListResponse,
    TimelapseUploadResponse,
    ErrorResponse
)
from app.modules.timelapse.dependencies import get_timelapse_repository
from app.modules.timelapse.repository.interface import TimelapseRepositoryInterface
from app.common.files import save_upload_file

router = APIRouter()


# === Dependencies ===
def get_service(
    repo: TimelapseRepositoryInterface = Depends(get_timelapse_repository)
) -> TimelapseService:
    return TimelapseService(repository=repo)


# === Endpoints ===

@router.get("", response_model=TimelapseListResponse, responses={
    401: {"model": ErrorResponse, "description": "인증 실패"},
    404: {"model": ErrorResponse, "description": "타임랩스 없음"}
})
async def get_timelapse_photos(
    plant_id: Optional[int] = None,
    service: TimelapseService = Depends(get_service)
):
    """
    타임랩스 사진 목록 조회
    taken_at 기준 오름차순(과거→현재)으로 반환합니다.
    """
    photos = await service.get_timelapse_photos(plant_id=plant_id)
    
    if not photos:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "TIMELAPSE_NOT_FOUND",
                "message": "재생 가능한 타임랩스 데이터가 없습니다."
            }
        )
    
    return TimelapseListResponse(
        total_frames=len(photos),
        photos=photos
    )


@router.post("", response_model=TimelapseUploadResponse, status_code=status.HTTP_201_CREATED, responses={
    400: {"model": ErrorResponse, "description": "필수 필드 누락"},
    413: {"model": ErrorResponse, "description": "파일 용량 초과"}
})
async def upload_timelapse_photo(
    image: UploadFile = File(..., description="타임랩스 사진 파일"),
    taken_at: datetime = Form(..., description="촬영 시간 (ISO 8601)"),
    plant_id: Optional[int] = Form(None, description="식물 ID (선택)"),
    service: TimelapseService = Depends(get_service)
):
    """
    타임랩스 사진 업로드 (서버/관리자용)
    자동 촬영 시스템 또는 수동 업로드 시 사용합니다.
    """
    # 이미지 저장
    image_url = await save_upload_file(image)
    
    if not image_url:
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={
                "error_code": "FILE_SAVE_ERROR",
                "message": "이미지 저장에 실패했습니다."
            }
        )
    
    result = await service.upload_photo(
        image_url=image_url,
        taken_at=taken_at,
        plant_id=plant_id
    )
    return result


@router.delete("/{photo_id}", status_code=status.HTTP_204_NO_CONTENT, responses={
    404: {"model": ErrorResponse, "description": "사진을 찾을 수 없음"}
})
async def delete_timelapse_photo(
    photo_id: int,
    service: TimelapseService = Depends(get_service)
):
    """
    타임랩스 사진 삭제
    성공 시 204 No Content를 반환합니다.
    """
    deleted = await service.delete_photo(photo_id)
    
    if not deleted:
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error_code": "PHOTO_NOT_FOUND",
                "message": "삭제할 사진을 찾을 수 없습니다."
            }
        )
    
    return Response(status_code=status.HTTP_204_NO_CONTENT)
