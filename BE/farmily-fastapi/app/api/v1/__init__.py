from fastapi import APIRouter
from app.api.v1.endpoints import ref_plants

router = APIRouter()

router.include_router(ref_plants.router, prefix="/ref-plants", tags=["Plants"])
