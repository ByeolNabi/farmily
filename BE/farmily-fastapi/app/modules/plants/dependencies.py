"""
Plants Module Dependencies
"""
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import settings
from app.core.database import get_db
from app.modules.plants.repository.interface import PlantRepositoryInterface
from app.modules.plants.repository.mock import MockPlantRepository
from app.modules.plants.repository.sql import SQLPlantRepository

def get_plant_repository(
    db: AsyncSession = Depends(get_db)
) -> PlantRepositoryInterface:
    """
    환경 변수에 따라 Mock 또는 SQL Repository를 주입하는 Factory 함수
    """
    if settings.USE_MOCK_DB:
        return MockPlantRepository()
    
    # 실제 DB 모드일 때는 DB 세션을 주입하여 SQL Repository 반환
    return SQLPlantRepository(session=db)
