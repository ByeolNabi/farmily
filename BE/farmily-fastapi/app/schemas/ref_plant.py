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
        
        # 1. Handle string format like "[18, 29)" or "[18, 28]"
        if isinstance(v, str):
            try:
                # Remove brackets/parentheses and split by comma
                cleaned = v.strip()[1:-1]
                parts = [p.strip() for p in cleaned.split(',')]
                if len(parts) != 2:
                    return None
                
                lower = int(parts[0])
                upper = int(parts[1])
                
                # Handle inclusive/exclusive upper bound
                # [18, 29) -> 18 to 28
                # [18, 28] -> 18 to 28
                if v.strip().endswith(')'):
                    upper = upper - 1
                
                return {"min": lower, "max": upper}
            except (ValueError, IndexError):
                return None

        # 2. Fallback for object with lower/upper attributes (psycopg2 Range)
        lower = getattr(v, 'lower', None)
        upper = getattr(v, 'upper', None)
        
        if lower is not None and upper is not None:
            return {"min": lower, "max": upper - 1}
            
        return v

class RefPlantSpeciesListResponse(BaseModel):
    total_count: int
    plants: List[RefPlantSpeciesSummaryResponse]
