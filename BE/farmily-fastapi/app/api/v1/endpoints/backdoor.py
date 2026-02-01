from datetime import timedelta
from typing import Any
from fastapi import APIRouter, HTTPException, Depends
from fastapi.security import OAuth2PasswordRequestForm
from pydantic import BaseModel
from app.core import security

router = APIRouter()

class BackdoorLoginRequest(BaseModel):
    user_id: int

@router.post("/backdoor")
def login_backdoor(request: BackdoorLoginRequest):
    """
    Backdoor login for development.
    Generates an access token with 365 days expiration.
    """
    return _generate_token(request.user_id)

@router.post("/token")
def login_oauth(form_data: OAuth2PasswordRequestForm = Depends()):
    """
    Standard OAuth2 token endpoint for Swagger UI 'Authorize' button.
    Use user_id in the 'username' field (password is ignored).
    """
    try:
        user_id = int(form_data.username)
        return _generate_token(user_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Username must be a valid User ID (Integer)")

def _generate_token(user_id: int):
    access_token_expires = timedelta(days=365)
    
    user_payload = {
        "sub": str(user_id),
        "user_id": user_id,
        "role": "user"
    }
    
    access_token = security.create_access_token(
        subject=user_payload,
        expires_delta=access_token_expires,
    )
    
    return {
        "access_token": access_token,
        "token_type": "bearer",
    }
