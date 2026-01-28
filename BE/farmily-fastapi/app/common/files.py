"""
File Processing Utilities
"""
import os
import shutil
import uuid
from typing import Optional
from pathlib import Path
from fastapi import UploadFile

from app.core.config import settings

# 이미지 저장 경로 (프로젝트 루트/static/images)
BASE_DIR = Path(__file__).resolve().parent.parent.parent
STATIC_DIR = BASE_DIR / "static"
UPLOAD_DIR = STATIC_DIR / "images"

# 디렉토리가 없으면 생성
if not UPLOAD_DIR.exists():
    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

async def save_upload_file(file: UploadFile) -> Optional[str]:
    """
    업로드된 파일을 로컬 스토리지에 저장하고 URL을 반환합니다.
    """
    if not file:
        return None
        
    # 고유 파일명 생성
    file_ext = os.path.splitext(file.filename)[1]
    unique_filename = f"{uuid.uuid4()}{file_ext}"
    file_path = UPLOAD_DIR / unique_filename
    
    # 파일 저장
    try:
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
    except Exception as e:
        print(f"File save error: {e}")
        return None
        
    # URL 반환 (예: http://localhost:8000/static/images/uuid.jpg)
    # 실제 운영 환경에서는 도메인을 환경변수에서 가져와야 함
    
    # settings.HOST가 '0.0.0.0'인 경우 localhost로 치환
    host = settings.HOST if settings.HOST != "0.0.0.0" else "localhost"
    base_url = f"http://{host}:{settings.PORT}"
    
    return f"{base_url}/static/images/{unique_filename}"
