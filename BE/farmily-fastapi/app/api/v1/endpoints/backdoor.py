from datetime import timedelta
from typing import Any
from fastapi import APIRouter, HTTPException
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
    access_token_expires = timedelta(days=365)
    
    # Payload as per design doc: sub, user_id, role, etc.
    # We will minimalistically satisfy the requirement: user_id
    user_payload = {
        "sub": str(request.user_id),
        "user_id": request.user_id,
        "role": "user" # Defaulting to user for now
    }
    
    access_token = security.create_access_token(
        subject=user_payload,
        expires_delta=access_token_expires,
    )
    
    return {
        "access_token": access_token,
        "token_type": "bearer",
    }
