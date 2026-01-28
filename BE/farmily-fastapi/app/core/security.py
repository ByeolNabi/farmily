"""
Security Core - JWT Authentication & Dev Mode Bypass
"""
from typing import Optional, Dict
from fastapi import Depends, HTTPException, status, Header
import jwt
from app.core.config import settings

async def get_current_user(
    authorization: Optional[str] = Header(None),
    x_dev_user_id: Optional[str] = Header(None)
) -> Dict:
    """
    현재 사용자 정보를 반환하는 의존성 함수.
    AUTH_ENABLED가 False이면(Dev Mode):
      - x-dev-user-id 헤더가 있으면 해당 ID 사용
      - 없으면 기본값(1) 사용
    True이면 JWT 토큰을 검증.
    """
    
    # 1. Dev Mode Bypass
    if not settings.AUTH_ENABLED:
        user_id = 1
        if x_dev_user_id and x_dev_user_id.isdigit():
            user_id = int(x_dev_user_id)
        return {"user_id": user_id, "role": "dev_user"}

    # 2. Token Extraction
    if not authorization:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing Authorization Header",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    try:
        scheme, token = authorization.split()
        if scheme.lower() != "bearer":
             raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid Authentication Scheme",
                headers={"WWW-Authenticate": "Bearer"},
            )
    except ValueError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid Authorization Header Format",
            headers={"WWW-Authenticate": "Bearer"},
        )

    # 3. JWT Verification
    try:
        payload = jwt.decode(
            token,
            settings.SECRET_KEY,
            algorithms=["HS256"]
        )
        # 필요한 검증 로직 추가 (exp, etc.) - PyJWT가 기본적으로 처리함
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token has expired",
            headers={"WWW-Authenticate": "Bearer"},
        )
    except jwt.InvalidTokenError:
         raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token",
            headers={"WWW-Authenticate": "Bearer"},
        )
