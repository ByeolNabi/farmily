from pydantic import BaseModel, ConfigDict
from datetime import datetime
from typing import List, Optional

class PlantDiaryBase(BaseModel):
    content: str
    happened_at: datetime
    # plant_id is passed separately or part of create

class PlantDiaryResponse(BaseModel):
    id: int
    plant_id: int
    content: Optional[str] = None
    image_url: Optional[str] = None
    happened_at: datetime
    created_at: datetime
    updated_at: datetime
    
    model_config = ConfigDict(from_attributes=True)

class PlantDiaryListResponse(BaseModel):
    total_count: int
    diaries: List[PlantDiaryResponse]

class PlantDiaryUpdate(BaseModel):
    content: Optional[str] = None
    happened_at: Optional[datetime] = None
