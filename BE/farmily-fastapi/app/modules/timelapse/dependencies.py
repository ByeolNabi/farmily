"""
Timelapse Module Dependencies
"""
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import settings
from app.core.database import get_db
from app.modules.timelapse.repository.interface import TimelapseRepositoryInterface
from app.modules.timelapse.repository.mock import MockTimelapseRepository


def get_timelapse_repository(
    db: AsyncSession = Depends(get_db)
) -> TimelapseRepositoryInterface:
    """
    환경 변수에 따라 Mock 또는 SQL Repository를 주입하는 Factory 함수
    """
    if settings.USE_MOCK_DB:
        return MockTimelapseRepository()
    
    # TODO: 실제 DB 모드일 때는 SQLTimelapseRepository 반환
    return MockTimelapseRepository()  # Fallback
