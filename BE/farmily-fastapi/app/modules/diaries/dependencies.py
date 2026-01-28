"""
Diaries Module Dependencies
"""
from typing import Dict
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import settings
from app.core.database import get_db
from app.modules.diaries.repository.interface import DiaryRepositoryInterface
from app.modules.diaries.repository.mock import MockDiaryRepository
# from app.modules.diaries.repository.sql import SQLDiaryRepository  # TODO: 실제 DB 구현 시 활성화


def get_diary_repository(
    db: AsyncSession = Depends(get_db)
) -> DiaryRepositoryInterface:
    """
    환경 변수에 따라 Mock 또는 SQL Repository를 주입하는 Factory 함수
    """
    if settings.USE_MOCK_DB:
        return MockDiaryRepository()
    
    from app.modules.diaries.repository.sql import SQLDiaryRepository
    return SQLDiaryRepository(session=db)


from app.core.security import get_current_user
