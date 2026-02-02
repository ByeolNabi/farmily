from fastapi import APIRouter
from app.api.v1.endpoints import ref_plants
from app.api.v1.endpoints import backdoor
from app.api.v1.endpoints import diaries
from app.api.v1.endpoints import mypage
from app.api.v1.endpoints import timelapse
from app.api.v1.endpoints import internal

router = APIRouter()

router.include_router(ref_plants.router, prefix="/ref-plants", tags=["Plants"])
router.include_router(backdoor.router, prefix="/auth", tags=["Auth"])
router.include_router(diaries.router, prefix="/diaries", tags=["Diaries"])
router.include_router(mypage.router, prefix="/pages/mypage", tags=["Pages"])
router.include_router(timelapse.router, prefix="/timelapse", tags=["Timelapse"])
router.include_router(internal.router, tags=["Internal"])

