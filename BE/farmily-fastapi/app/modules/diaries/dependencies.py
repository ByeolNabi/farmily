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


async def get_current_user() -> Dict[str, int]:
    """
    현재 인증된 사용자 정보를 반환합니다.
    
    TODO: 실제 JWT 인증 구현 시 이 함수를 수정하세요.
    현재는 Mock으로 user_id=1을 반환합니다.
    """
    # Mock 사용자 (실제 JWT 구현 시 아래 코드로 교체)
    # from fastapi import HTTPException, status
    # from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
    # from jose import jwt, JWTError
    
    return {"user_id": 1}
