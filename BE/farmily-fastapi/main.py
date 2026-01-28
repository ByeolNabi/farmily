"""
Farmily API - Main Application Entry Point
"""
from contextlib import asynccontextmanager
from pathlib import Path
from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from fastapi.middleware.cors import CORSMiddleware

from app.core.config import settings
from app.core.logger import logger
from app.modules.edge_devices.router import router as edge_devices_router
from app.modules.sensors.router import router as sensors_router
from app.modules.ai_inference.router import router as ai_inference_router
from app.modules.plants.router import router as plants_router
from app.modules.pages.router import router as pages_router
from app.modules.diaries.router import router as diaries_router
from app.modules.timelapse.router import router as timelapse_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    """앱 시작/종료 시 리소스 관리"""
    # Startup
    logger.info("🚀 Starting Farmily API Server...")
    logger.info(f"📌 Environment: {settings.APP_ENV}")
    logger.info(f"📌 Debug Mode: {settings.DEBUG}")
    
    # TODO: DB 연결 시 활성화
    from app.core.database import engine
    from app.core.database import Base
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    
    # TODO: MQTT 연결 시 활성화
    # from app.infra.mqtt_client import mqtt_client
    # await mqtt_client.connect()
    
    yield
    
    # Shutdown
    logger.info("🛑 Shutting down Farmily API Server...")
    # await mqtt_client.disconnect()


app = FastAPI(
    title=settings.APP_NAME,
    description="🌱 Farmily - Smart Farm Management API",
    version="0.1.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Production에서는 특정 origin만 허용
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 정적 파일 마운트 (이미지 서빙)
STATIC_DIR = Path(__file__).resolve().parent / "static"
if not STATIC_DIR.exists():
    STATIC_DIR.mkdir(parents=True, exist_ok=True)
    
app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")

# 라우터 등록
app.include_router(edge_devices_router, prefix="/api/v1/devices", tags=["Edge Devices"])
app.include_router(sensors_router, prefix="/api/v1/sensors", tags=["Sensors"])
app.include_router(ai_inference_router, prefix="/api/v1/ai", tags=["AI Inference"])
app.include_router(plants_router, prefix="/api/v1/plants", tags=["Plants"])
app.include_router(pages_router, prefix="/api/v1/pages", tags=["Pages"])
app.include_router(diaries_router, prefix="/api/v1/diaries", tags=["Diaries"])
app.include_router(timelapse_router, prefix="/api/v1/timelapse", tags=["Timelapse"])


@app.get("/", tags=["Health"])
async def root():
    """API 상태 확인"""
    return {
        "status": "healthy",
        "app": settings.APP_NAME,
        "version": "0.1.0",
        "message": "🌱 Welcome to Farmily API!"
    }


@app.get("/health", tags=["Health"])
async def health_check():
    """상세 헬스체크"""
    return {
        "status": "healthy",
        "services": {
            "api": "up",
            "database": "connected",  # DB 연결 시 실제 상태로 변경
            "mqtt": "mock"       # MQTT 연결 시 실제 상태로 변경
        }
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host=settings.HOST,
        port=settings.PORT,
        reload=settings.DEBUG
    )
