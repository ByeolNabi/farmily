from fastapi import APIRouter
from app.api.v1.endpoints import ref_plants
from app.api.v1.endpoints import backdoor

router = APIRouter()

router.include_router(ref_plants.router, prefix="/ref-plants", tags=["Plants"])
router.include_router(backdoor.router, prefix="/auth", tags=["Auth"])
