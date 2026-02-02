"""
JWT Utility for internal service API calls.
Generates JWT tokens for server-to-server authentication.
"""
import jwt
from datetime import datetime, timedelta
from typing import Optional
from loguru import logger

from app.core.config import settings


def generate_service_jwt(user_id: int, expires_days: int = 365) -> str:
    """Generate a JWT token for service-to-service API calls.
    
    Args:
        user_id: The user ID to include in the token
        expires_days: Token expiration in days (default: 1 year)
    
    Returns:
        JWT token string
    """
    now = datetime.utcnow()
    payload = {
        "sub": f"user_{user_id}",
        "user_id": user_id,
        "role": "service",  # Mark as service account
        "iat": int(now.timestamp()),
        "exp": int((now + timedelta(days=expires_days)).timestamp()),
    }
    
    token = jwt.encode(payload, settings.SECRET_KEY, algorithm=settings.ALGORITHM)
    logger.debug(f"[JWT] Generated service token for user_id={user_id}")
    return token


class ServiceAuthManager:
    """Manages JWT token for service API calls."""
    
    _instance: Optional["ServiceAuthManager"] = None
    
    def __new__(cls) -> "ServiceAuthManager":
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        
        self._initialized = True
        self._token: Optional[str] = None
        self._user_id: Optional[int] = None
        logger.info("[ServiceAuth] Manager initialized")
    
    def set_user_id(self, user_id: int) -> None:
        """Set user ID and generate token."""
        self._user_id = user_id
        self._token = generate_service_jwt(user_id)
        logger.info(f"[ServiceAuth] Token generated for user_id={user_id}")
    
    def get_auth_headers(self) -> dict:
        """Get authorization headers for API requests.
        
        Returns:
            Dict with Authorization header, or empty dict if no token
        """
        if self._token:
            return {"Authorization": f"Bearer {self._token}"}
        return {}
    
    @property
    def token(self) -> Optional[str]:
        return self._token
    
    @property
    def user_id(self) -> Optional[int]:
        return self._user_id


# Singleton instance
service_auth = ServiceAuthManager()
