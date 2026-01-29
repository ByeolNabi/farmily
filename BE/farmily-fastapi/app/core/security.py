from datetime import datetime, timedelta, timezone
from typing import Any
import jwt
from app.core.config import settings

def create_access_token(subject: str | Any, expires_delta: timedelta = None) -> str:
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    
    to_encode = {"exp": expire, "sub": str(subject)}
    
    if isinstance(subject, dict):
        to_encode.update(subject)
        # Ensure 'sub' is a string if it was overwritten or if we need it specifically
        if "sub" not in subject:
             # If subject was a dict but didn't have sub, we might have an issue if we just used str(subject) above.
             # But usually subject is just the ID if it's not a dict, or a dict of claims.
             # If it is a dict, we probably want to use a specific field as sub, or just keep the one we set.
             pass
    
    # PyJWT 2.x encode returns a string
    encoded_jwt = jwt.encode(to_encode, settings.SECRET_KEY, algorithm=settings.ALGORITHM)
    return encoded_jwt
