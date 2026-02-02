from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from loguru import logger
import os
import app.models  # Register all models

from app.core.config import settings
from app.mqtt.client import mqtt_client
from app.mqtt.handlers.sensor_handler import register_sensor_handler
from app.mqtt.handlers.device_handler import register_device_event_handler
from app.mqtt.handlers.jetson_handler import register_jetson_handler
from app.mqtt.services.light_control_service import light_control_service


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan manager for startup/shutdown events."""
    # Startup
    logger.info("Starting Farmily API...")
    
    # Configure logging (Filter logs below INFO level)
    import sys
    logger.remove()
    logger.add(sys.stderr, level="INFO")
    
    # Connect MQTT client
    try:
        await mqtt_client.connect()
        
        # Set MQTT client reference in light control service
        light_control_service.set_mqtt_client(mqtt_client)
        
        # Register handlers
        register_sensor_handler(mqtt_client)        # Fixed telemetry topic
        register_device_event_handler(mqtt_client)  # Generic device events
        register_jetson_handler(mqtt_client)        # Jetson position tracking
        
        logger.info("MQTT client connected and all handlers registered")
    except Exception as e:
        logger.error(f"Failed to connect MQTT: {e}")
    
    yield
    
    # Shutdown
    logger.info("Shutting down Farmily API...")
    await mqtt_client.disconnect()


app = FastAPI(
    title=settings.APP_NAME,
    debug=settings.DEBUG,
    version="1.0.0",
    lifespan=lifespan,
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Ensure uploads directory exists and Mount Static Files
os.makedirs("uploads", exist_ok=True)
app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")


@app.get("/")
async def root():
    return {"message": "Welcome to Farmily API"}


@app.get("/health")
async def health_check():
    return {"status": "healthy"}


# Include routers
from app.api.v1 import router as api_v1_router
app.include_router(api_v1_router, prefix="/api/v1")
