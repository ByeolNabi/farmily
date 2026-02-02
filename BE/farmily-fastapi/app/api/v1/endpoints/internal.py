"""
Internal API endpoints for MQTT services.
These endpoints are used by internal services, not exposed to external clients.
"""
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from geoalchemy2.functions import ST_X, ST_Y

from app.core.database import get_db
from app.models.plant import Plant
from app.models.reference import RefPlantSpecies
from app.mqtt.config import MVP_USER_ID

router = APIRouter(prefix="/internal", tags=["internal"])


class PlantConfigResponse(BaseModel):
    """Response schema for plant configuration."""
    user_id: int
    plant_id: int
    plant_nickname: Optional[str]
    ref_plant_species_id: int
    ref_plant_name: Optional[str]
    illuminance_target: int
    station_x: Optional[float]
    station_y: Optional[float]


@router.get("/plant-config", response_model=PlantConfigResponse)
async def get_plant_config(db: AsyncSession = Depends(get_db)):
    """
    Get plant configuration for light control service.
    Returns the latest active plant for MVP_USER_ID (hardcoded to 1).
    """
    # Query: Get latest active plant with species info
    stmt = (
        select(
            Plant.id,
            Plant.nickname,
            Plant.ref_plant_species_id,
            Plant.station_point,
            RefPlantSpecies.name.label("species_name"),
            RefPlantSpecies.illuminance,
        )
        .join(RefPlantSpecies, Plant.ref_plant_species_id == RefPlantSpecies.id)
        .where(Plant.users_id == MVP_USER_ID)
        .where(Plant.is_active == True)
        .order_by(Plant.created_at.desc())
        .limit(1)
    )
    
    result = await db.execute(stmt)
    row = result.first()
    
    if not row:
        raise HTTPException(status_code=404, detail="No active plant found for user")
    
    # Extract station coordinates from PostGIS point
    station_x = None
    station_y = None
    
    if row.station_point is not None:
        # Extract x, y from geometry
        coord_stmt = select(
            ST_X(Plant.station_point).label("x"),
            ST_Y(Plant.station_point).label("y")
        ).where(Plant.id == row.id)
        coord_result = await db.execute(coord_stmt)
        coord_row = coord_result.first()
        if coord_row:
            station_x = coord_row.x
            station_y = coord_row.y
    
    return PlantConfigResponse(
        user_id=MVP_USER_ID,
        plant_id=row.id,
        plant_nickname=row.nickname,
        ref_plant_species_id=row.ref_plant_species_id,
        ref_plant_name=row.species_name,
        illuminance_target=row.illuminance or 8000,  # Default fallback
        station_x=station_x,
        station_y=station_y,
    )
