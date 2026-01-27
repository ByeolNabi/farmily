"""
Pages Module Dependencies
"""
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import settings
from app.core.database import get_db
from app.modules.pages.repository.interface import PageRepositoryInterface
from app.modules.pages.repository.mock import MockPageRepository
from app.modules.pages.repository.sql import SQLPageRepository

def get_page_repository(
    db: AsyncSession = Depends(get_db)
) -> PageRepositoryInterface:
    """
    환경 변수(USE_MOCK_DB)에 따라 Repository 구현체 주입
    """
    if settings.USE_MOCK_DB:
        return MockPageRepository()
    
    return SQLPageRepository(session=db)
