from datetime import datetime
from typing import List, Tuple, Optional
from fastapi import UploadFile, HTTPException, status

from app.models.plant_log import PlantDiary
from app.repositories.plant_diary_repository import PlantDiaryRepository
from app.services.file_service import FileService

class PlantDiaryService:
    def __init__(self, repo: PlantDiaryRepository, file_service: FileService):
        self.repo = repo
        self.file_service = file_service

    async def get_diaries(self, user_id: int, plant_id: Optional[int] = None, skip: int = 0, limit: int = 100) -> Tuple[List[PlantDiary], int]:
        """
        Get all diaries for a user, optionally filtered by plant_id.
        Returns (list of diaries, total_count).
        """
        # Security: By passing user_id to repo, we strictly filter to plants owned by the user.
        # This handles the requirement that users see only their own diaries.
        
        diaries = await self.repo.get_all(skip, limit, plant_id, user_id)
        count = await self.repo.count(plant_id, user_id)
        return diaries, count

    async def get_diary_detail(self, diary_id: int, user_id: int) -> PlantDiary:
        diary = await self.repo.get_by_id(diary_id)
        if not diary:
            raise HTTPException(status_code=404, detail="DIARY_NOT_FOUND")
            
        # Check ownership
        owner_id = await self.repo.get_diary_owner_id(diary_id)
        if owner_id != user_id:
            # Different user's diary
            raise HTTPException(status_code=401, detail="UNAUTHORIZED")
            
        return diary

    async def create_diary(self, user_id: int, plant_id: int, content: str, happened_at: datetime, image: Optional[UploadFile] = None) -> PlantDiary:
        if not content:
             raise HTTPException(status_code=400, detail="REQUIRED_CONTENT_MISSING")
             
        # Check plant ownership
        owner_id = await self.repo.get_plant_owner_id(plant_id)
        if not owner_id:
             # Plant implies "Plant not found" (or doesn't exist)
             raise HTTPException(status_code=404, detail="PLANT_NOT_FOUND") 
        
        if owner_id != user_id:
             raise HTTPException(status_code=401, detail="UNAUTHORIZED")

        image_url = None
        if image:
            # Basic size check could happen here if reading file metadata/chunks, 
            # but standard UploadFile has .size attribute (if spool is used) or we check content-length header
            # For now relying on server config or basic check in file service.
            image_url = await self.file_service.save_image(image)

        diary = PlantDiary(
            plant_id=plant_id,
            content=content,
            image_url=image_url,
            happened_at=happened_at
        )
        return await self.repo.create(diary)

    async def update_diary(self, user_id: int, diary_id: int, content: Optional[str] = None, happened_at: Optional[datetime] = None, image: Optional[UploadFile] = None) -> PlantDiary:
        diary = await self.repo.get_by_id(diary_id)
        if not diary:
            raise HTTPException(status_code=404, detail="DIARY_NOT_FOUND")
            
        owner_id = await self.repo.get_diary_owner_id(diary_id)
        if owner_id != user_id:
            raise HTTPException(status_code=401, detail="UNAUTHORIZED")
            
        if content is not None:
             diary.content = content
        if happened_at is not None:
             diary.happened_at = happened_at
        
        if image:
             # Delete old image if it exists
             if diary.image_url:
                 await self.file_service.delete_image(diary.image_url)
             
             diary.image_url = await self.file_service.save_image(image)
             
        # Commit is handled by session management usually, but here we need to flush/commit
        # Since repo methods are atomic-ish, we reuse the session.
        # Simple update:
        await self.repo.db.commit()
        await self.repo.db.refresh(diary)
        return diary

    async def delete_diary(self, user_id: int, diary_id: int):
        diary = await self.repo.get_by_id(diary_id)
        if not diary:
            raise HTTPException(status_code=404, detail="DIARY_NOT_FOUND")
            
        owner_id = await self.repo.get_diary_owner_id(diary_id)
        if owner_id != user_id:
            raise HTTPException(status_code=401, detail="UNAUTHORIZED")
            
        # Delete image file
        if diary.image_url:
            await self.file_service.delete_image(diary.image_url)
            
        await self.repo.delete(diary_id)
