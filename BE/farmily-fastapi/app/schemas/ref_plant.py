from typing import Optional, List, Any
from pydantic import BaseModel, ConfigDict

class RefPlantSpeciesBase(BaseModel):
    name: str
    image_url: Optional[str] = None
    
    model_config = ConfigDict(from_attributes=True)

class RefPlantSpeciesSummaryResponse(RefPlantSpeciesBase):
    id: int

class RefPlantSpeciesDetailResponse(RefPlantSpeciesBase):
    id: int
    temp_target: Optional[int] = None
    temp_range: Any = None 
    humid_target: Optional[int] = None
    humid_range: Any = None
    soil_target: Optional[int] = None
    soil_range: Any = None
    illuminance: Optional[int] = None

class RefPlantSpeciesListResponse(BaseModel):
    total_count: int
    plants: List[RefPlantSpeciesSummaryResponse]
