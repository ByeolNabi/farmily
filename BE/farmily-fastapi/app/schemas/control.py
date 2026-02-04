from pydantic import BaseModel, Field

class JetsonMoveRequest(BaseModel):
    x: float = Field(..., description="Target X coordinate")
    y: float = Field(..., description="Target Y coordinate")
    theta: float = Field(..., description="Target angle (theta)")
