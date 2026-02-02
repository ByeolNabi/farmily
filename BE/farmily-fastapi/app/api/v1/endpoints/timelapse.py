from datetime import datetime
from typing import Annotated, Optional

from fastapi import APIRouter, Depends, UploadFile, File, Form, Query, status, Response, HTTPException

from app.api.deps import CurrentUser, DbSession
from app.repositories.timelapse_repository import TimelapseRepository
from app.services.timelapse_service import TimelapseService
from app.services.file_service import FileService
from app.schemas.timelapse import TimelapseListResponse, TimelapseCreateResponse, TimelapsePhotoSchema

router = APIRouter()

# Dependency Injection helper
async def get_service(db: DbSession) -> TimelapseService:
    repo = TimelapseRepository(db)
    file_service = FileService()
    return TimelapseService(repo, file_service)

Service = Annotated[TimelapseService, Depends(get_service)]

@router.get("", response_model=TimelapseListResponse, description="Get timelapse photos for a specific plant.")
async def get_timelapse_list(
    service: Service,
    plant_id: int = Query(..., description="Filter by plant ID")
):
    """타임랩스 사진 목록 조회"""
    photos, total_frames = await service.get_timelapses(plant_id)
    
    if not photos:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="재생 가능한 타임랩스 데이터가 없습니다."
        )
    
    # Pydantic 모델로 변환
    photo_list = [TimelapsePhotoSchema.model_validate(p) for p in photos]
    
    return TimelapseListResponse(
        total_frames=total_frames,
        photos=photo_list
    )

@router.post("", response_model=TimelapseCreateResponse, status_code=status.HTTP_201_CREATED)
async def create_timelapse(
    service: Service,
    plant_id: int = Form(..., description="연결할 식물 ID"),
    created_at: Optional[str] = Form(None, description="촬영 시점 (ISO 8601)"),
    image: UploadFile = File(..., description="타임랩스 사진 파일")
):
    """타임랩스 사진 업로드"""
    
    # 1. 파일 크기 체크 (10MB)
    MAX_FILE_SIZE = 10 * 1024 * 1024
    if image.size and image.size > MAX_FILE_SIZE:
        raise HTTPException(
            status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
            detail="이미지 파일이 너무 큽니다. (최대 10MB)"
        )
    
    # 2. 시간 파싱
    if created_at:
        try:
            taken_at = datetime.fromisoformat(created_at)
        except ValueError:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="촬영 시간(taken_at) 정보가 올바르지 않습니다."
            )
    else:
        taken_at = datetime.now()

    try:
        created_timelapse = await service.create_timelapse(
            plant_id=plant_id,
            image=image,
            created_at=taken_at
        )
    except Exception as e:
        # 파일 저장 실패 등
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="이미지 저장에 실패했습니다."
        )
    
    return TimelapseCreateResponse.model_validate(created_timelapse)

@router.delete("/{photo_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_timelapse(
    photo_id: int,
    service: Service,
    # current_user: CurrentUser # 삭제 권한 체크가 필요할 수 있으나 명세에는 언급 없음. 일단 추가해둠.
):
    """타임랩스 사진 삭제"""
    await service.delete_timelapse(photo_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
