import os
import shutil
from pathlib import Path
from fastapi import UploadFile, HTTPException
from uuid import uuid4

# Define upload directory. 
# We'll place it at the project root level, relative to where the app runs.
UPLOAD_DIR = Path("uploads")

class FileService:
    def __init__(self):
        UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

    async def save_image(self, file: UploadFile) -> str:
        """
        Save an uploaded file to the local filesystem and return its public URL path.
        """
        # Validate file type (basic check)
        if not file.content_type.startswith("image/"):
             raise HTTPException(status_code=400, detail="INVALID_FILE_TYPE")

        # Generate unique filename
        # Clean filename to avoid issues
        original_filename = file.filename or "image.jpg"
        extension = os.path.splitext(original_filename)[1]
        unique_name = f"{uuid4()}{extension}"
        file_path = UPLOAD_DIR / unique_name
        
        try:
            # We use synchronous file writing here for simplicity with UploadFile
            # Ideally verify if async write is needed, but shutil is sync.
            # Given standard FastAPI UploadFile, reading into memory/saving:
            with open(file_path, "wb") as buffer:
                shutil.copyfileobj(file.file, buffer)
        except Exception as e:
            raise HTTPException(status_code=500, detail="FILE_SAVE_ERROR")
            
        return f"/uploads/{unique_name}"

    async def delete_image(self, image_url: str):
        """
        Delete an image file from the filesystem.
        """
        if not image_url:
            return
            
        try:
            # Assumes url format "/uploads/filename.jpg"
            if "/uploads/" not in image_url:
                return

            filename = image_url.split("/uploads/")[-1]
            file_path = UPLOAD_DIR / filename
            
            if file_path.exists():
                os.remove(file_path)
        except Exception:
            # Log error but don't fail the request
            pass
