"""
Timelapse - 서비스 로직
"""
from typing import Optional, List
from datetime import datetime

from app.modules.timelapse.schemas import TimelapsePhoto, TimelapseUploadResponse
from app.modules.timelapse.repository.interface import TimelapseRepositoryInterface


class TimelapseService:
    """타임랩스 관리 서비스"""
    
    def __init__(self, repository: TimelapseRepositoryInterface):
        self.repository = repository
    
    async def get_timelapse_photos(
        self, 
        plant_id: Optional[int] = None
    ) -> List[TimelapsePhoto]:
        """타임랩스 사진 목록 조회 (taken_at 오름차순, frame_no 부여)"""
        raw_data = await self.repository.get_all(plant_id)
        
        return [
            TimelapsePhoto(
                frame_no=idx,
                image_url=item["image_url"],
                taken_at=item["taken_at"]
            )
            for idx, item in enumerate(raw_data)
        ]
    
    async def upload_photo(
        self,
        image_url: str,
        taken_at: datetime,
        plant_id: Optional[int] = None
    ) -> TimelapseUploadResponse:
        """새 타임랩스 사진 업로드"""
        created = await self.repository.create(
            image_url=image_url,
            taken_at=taken_at,
            plant_id=plant_id
        )
        
        # frame_no는 현재 총 개수 기준으로 계산
        all_photos = await self.repository.get_all(plant_id)
        frame_no = len(all_photos) - 1
        
        return TimelapseUploadResponse(
            photo_id=created["id"],
            frame_no=frame_no,
            image_url=created["image_url"],
            taken_at=created["taken_at"]
        )
    
    async def delete_photo(self, photo_id: int) -> bool:
        """타임랩스 사진 삭제"""
        return await self.repository.delete(photo_id)
