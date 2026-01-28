"""
Diaries - SQL Repository implementation
"""
from typing import List, Optional, Any
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, update, delete, desc

from app.modules.diaries.repository.interface import DiaryRepositoryInterface
from app.modules.diaries.models import PlantDiary
from app.modules.plants.models import Plant 

class SQLDiaryRepository(DiaryRepositoryInterface):
    """실제 DB를 사용하는 리포지토리"""
    
    def __init__(self, session: AsyncSession):
        self.session = session
        
    async def get_all(self, owner_id: int, plant_id: Optional[int] = None) -> List[Any]:
        """
        사용자의 모든 일기 또는 특정 식물의 일기 조회
        TODO: owner_id를 이용해 Plant 테이블과 조인하여 내 식물의 일기만 가져오는 로직이 이상적이나,
        현재는 plant_id가 주어지면 해당 식물의 일기를, 아니면 전체를 가져오게 구현 (임시)
        """
        stmt = select(PlantDiary).join(Plant, PlantDiary.plant_id == Plant.id)
        stmt = stmt.where(Plant.users_id == owner_id)
        
        if plant_id:
            stmt = stmt.where(PlantDiary.plant_id == plant_id)
        
        # 최신순 정렬
        stmt = stmt.order_by(desc(PlantDiary.recorded_at))
        
        result = await self.session.execute(stmt)
        diaries = result.scalars().all()
        
        # Pydantic 스키마와 호환되는 dict 형태로 변환
        return [self._to_dict(d) for d in diaries]

    async def get_by_id(self, diary_id: int, owner_id: int) -> Optional[Any]:
        """일기 상세 조회"""
        # TODO: owner_id 체크 로직 필요 (내 식물의 일기인지)
        stmt = select(PlantDiary).join(Plant, PlantDiary.plant_id == Plant.id)
        stmt = stmt.where(PlantDiary.id == diary_id, Plant.users_id == owner_id)
        
        result = await self.session.execute(stmt)
        diary = result.scalar_one_or_none()
        
        if diary:
            return self._to_dict(diary)
        return None

    async def create(self, owner_id: int, content: str, recorded_at: Any, image_url: Optional[str] = None, plant_id: Optional[int] = None) -> Any:
        # plant_id는 필수여야 함 (Service 계층에서 체크하겠지만 여기서도 반영)
        # plant_id는 필수여야 함 (Service 계층에서 체크하겠지만 여기서도 반영)
        if not plant_id:
            raise ValueError("plant_id is required for creating a diary")

        # 소유권 체크
        plant_stmt = select(Plant).where(Plant.id == plant_id, Plant.users_id == owner_id)
        plant_result = await self.session.execute(plant_stmt)
        plant = plant_result.scalar_one_or_none()
        
        if not plant:
             raise ValueError("Plant not found or access denied")

        new_diary = PlantDiary(
            plant_id=plant_id,
            content=content,
            recorded_at=recorded_at,
            image_url=image_url
        )
        self.session.add(new_diary)
        await self.session.flush() # ID 생성을 위해 flush
        await self.session.refresh(new_diary)
        
        return self._to_dict(new_diary)

    async def update(self, diary_id: int, owner_id: int, content: Optional[str] = None, recorded_at: Optional[Any] = None, image_url: Optional[str] = None) -> Optional[Any]:
        # TODO: owner_id 체크
        stmt = select(PlantDiary).join(Plant, PlantDiary.plant_id == Plant.id)
        stmt = stmt.where(PlantDiary.id == diary_id, Plant.users_id == owner_id)
        
        result = await self.session.execute(stmt)
        diary = result.scalar_one_or_none()
        
        if not diary:
            return None
        
        if content is not None:
            diary.content = content
        if recorded_at is not None:
            diary.recorded_at = recorded_at
        if image_url is not None:
            diary.image_url = image_url
            
        await self.session.flush()
        await self.session.refresh(diary)
        return self._to_dict(diary)

    async def delete(self, diary_id: int, owner_id: int) -> bool:
        # TODO: owner_id 체크
        stmt = select(PlantDiary).join(Plant, PlantDiary.plant_id == Plant.id)
        stmt = stmt.where(PlantDiary.id == diary_id, Plant.users_id == owner_id)
        
        result = await self.session.execute(stmt)
        diary = result.scalar_one_or_none()
        
        if not diary:
            return False
            
        await self.session.delete(diary)
        return True

    def _to_dict(self, diary: PlantDiary) -> dict:
        """ORM 객체를 딕셔너리로 변환"""
        return {
            "id": diary.id,
            "plant_id": diary.plant_id,
            "content": diary.content,
            "image_url": diary.image_url,
            "recorded_at": diary.recorded_at,
            "created_at": diary.created_at,
            "updated_at": diary.created_at # created_at으로 대체 (update 컬럼이 따로 없으므로)
        }
