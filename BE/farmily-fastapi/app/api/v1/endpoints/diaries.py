from datetime import datetime
from typing import Annotated, Optional

from fastapi import APIRouter, Depends, Query, UploadFile, File, Form, status, Response, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.api import deps
from app.api.deps import CurrentUser, DbSession
from app.repositories.plant_diary_repository import PlantDiaryRepository
from app.services.plant_diary_service import PlantDiaryService
from app.services.file_service import FileService
from app.schemas.plant_diary import PlantDiaryResponse, PlantDiaryListResponse

router = APIRouter()

# Dependency Injection helper
async def get_service(db: DbSession) -> PlantDiaryService:
    repo = PlantDiaryRepository(db)
    file_service = FileService()
    return PlantDiaryService(repo, file_service)

Service = Annotated[PlantDiaryService, Depends(get_service)]

@router.get("", response_model=PlantDiaryListResponse, description="Get all diaries for the current user, optionally filtered by plant ID.")
async def get_diaries(
    service: Service,
    current_user: CurrentUser,
    plant_id: Optional[int] = Query(None, description="Filter by plant ID"),
    skip: int = 0,
    limit: int = 100
):
    diaries, total_count = await service.get_diaries(
        user_id=current_user.id,
        plant_id=plant_id,
        skip=skip,
        limit=limit
    )
    return {"total_count": total_count, "diaries": diaries}

@router.post("", response_model=PlantDiaryResponse, status_code=status.HTTP_200_OK)
async def create_diary(
    service: Service,
    current_user: CurrentUser,
    plant_id: int = Form(...),
    content: str = Form(...),
    happened_at: Optional[str] = Form(None),
    image: UploadFile = File(None)
):

    if happened_at:
        happened_at = datetime.fromisoformat(happened_at)
    else:
        happened_at = datetime.now()
        
    return await service.create_diary(
        user_id=current_user.id,
        plant_id=plant_id,
        content=content,
        happened_at=happened_at,
        image=image
    )

@router.get("/{diary_id}", response_model=PlantDiaryResponse, description="Get specific diary detail.")
async def get_diary_detail(
    diary_id: int,
    service: Service,
    current_user: CurrentUser
):
    return await service.get_diary_detail(diary_id, current_user.id)

@router.patch("/{diary_id}", response_model=PlantDiaryResponse)
async def update_diary(
    diary_id: int,
    service: Service,
    current_user: CurrentUser,
    content: Optional[str] = Form(None),
    happened_at: Optional[str] = Form(None),
    image: Optional[UploadFile] = File(None)
):

    if happened_at:
        happened_at = datetime.fromisoformat(happened_at)
    else:
        happened_at = datetime.now()

    return await service.update_diary(
        user_id=current_user.id,
        diary_id=diary_id,
        content=content,
        happened_at=happened_at,
        image=image
    )

@router.delete("/{diary_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_diary(
    diary_id: int,
    service: Service,
    current_user: CurrentUser
):
    await service.delete_diary(user_id=current_user.id, diary_id=diary_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
