from datetime import datetime
from typing import Optional, List, Any
from pydantic import BaseModel, ConfigDict, Field, field_validator

class RangeSchema(BaseModel):
    min: int
    max: int

class RefPlantSpeciesBase(BaseModel):
    name: str
    image_url: Optional[str] = None
    
    model_config = ConfigDict(from_attributes=True)

class RefPlantSpeciesSummaryResponse(RefPlantSpeciesBase):
    id: int

class RefPlantSpeciesDetailResponse(RefPlantSpeciesBase):
    id: int
    temp_target: Optional[int] = None
    temp_range: Optional[RangeSchema] = None
    humid_target: Optional[int] = None
    humid_range: Optional[RangeSchema] = None
    soil_target: Optional[int] = None
    soil_range: Optional[RangeSchema] = None
    illuminance: Optional[int] = None
    created_at: datetime
    
    @field_validator('temp_range', 'humid_range', 'soil_range', mode='before')
    @classmethod
    def parse_range(cls, v: Any) -> Optional[dict]:
        if v is None:
            return None
        
        # Check for psycopg2/SQLAlchemy Range object attributes
        # Postgres int4range is canonically [ ) (inclusive lower, exclusive upper)
        lower = getattr(v, 'lower', None)
        upper = getattr(v, 'upper', None)
        
        if lower is not None and upper is not None:
            # Convert exclusive upper bound to inclusive max for UI
            return {"min": lower, "max": upper - 1}
            
        return v

class RefPlantSpeciesListResponse(BaseModel):
    total_count: int
    plants: List[RefPlantSpeciesSummaryResponse]
