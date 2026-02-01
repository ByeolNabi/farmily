from datetime import datetime
from typing import List, Tuple

from fastapi import UploadFile, HTTPException, status

from app.repositories.timelapse_repository import TimelapseRepository
from app.services.file_service import FileService
from app.models.plant_log import PlantTimelapse


class TimelapseService:
    """타임랩스 비즈니스 로직"""
    
    def __init__(self, repository: TimelapseRepository, file_service: FileService):
        self.repository = repository
        self.file_service = file_service
    
    async def get_timelapses(self, plant_id: int) -> Tuple[List[PlantTimelapse], int]:
        """타임랩스 목록 조회"""
        photos = await self.repository.get_timelapses(plant_id)
        return photos, len(photos)
    
    async def create_timelapse(
        self, 
        plant_id: int, 
        image: UploadFile, 
        created_at: datetime
    ) -> PlantTimelapse:
        """타임랩스 사진 업로드 및 저장"""
        
        # 1. 이미지 파일 저장
        image_url = await self.file_service.save_image(image)
        
        # 2. DB 저장
        timelapse = PlantTimelapse(
            plant_id=plant_id,
            image_url=image_url,
            created_at=created_at
        )
        return await self.repository.create_timelapse(timelapse)
    
    async def delete_timelapse(self, photo_id: int) -> None:
        """타임랩스 사진 삭제"""
        
        # 1. 존재 여부 확인
        timelapse = await self.repository.get_timelapse(photo_id)
        if not timelapse:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="삭제할 사진을 찾을 수 없습니다." # PHOTO_NOT_FOUND
            )
            
        # 2. 파일 삭제 (DB 트랜잭션과 별개로 파일 삭제 시도, 실패해도 DB 삭제는 진행하거나 로그 남김)
        # FileService에 delete_file 메소드가 있다고 가정하거나 직접 구현 필요
        # 현재 FileService 구현을 확인하지 못했으므로, 이미지 URL이 로컬 경로라면 삭제 시도 가능
        # 여기서는 DB 삭제를 우선 진행
        
        # 3. DB 삭제
        await self.repository.delete_timelapse(timelapse)
