from datetime import datetime
from typing import List, Optional, TYPE_CHECKING
import enum

from sqlalchemy import BigInteger, Integer, String, Text, Enum, text, func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import INT4RANGE
from app.core.database import Base

if TYPE_CHECKING:
    from app.models.plant import Plant
    from app.models.plant_log import PlantAchievement, PlantHealthLog

class PlantActionType(str, enum.Enum):
    petting = "petting"
    watering = "watering"
    praising = "praising"
    talking = "talking"
    diary = "diary"

class RefPlantSpecies(Base):
    __tablename__ = "ref_plant_species"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    image_url: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    temp_target: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    temp_range: Mapped[Optional[object]] = mapped_column(INT4RANGE, nullable=True)
    humid_target: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    humid_range: Mapped[Optional[object]] = mapped_column(INT4RANGE, nullable=True)
    soil_target: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    soil_range: Mapped[Optional[object]] = mapped_column(INT4RANGE, nullable=True)
    illuminance: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)

    # Relationships
    plants: Mapped[List["Plant"]] = relationship("Plant", back_populates="species")

class RefAchievement(Base):
    __tablename__ = "ref_achievement"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    icon_url: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    action_type: Mapped[Optional[PlantActionType]] = mapped_column(Enum(PlantActionType, name="plant_action_type"), nullable=True)
    required_count: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)

    # Relationships
    plant_achievements: Mapped[List["PlantAchievement"]] = relationship("PlantAchievement", back_populates="achievement")

class RefPlantDisease(Base):
    __tablename__ = "ref_plant_disease"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    symptoms: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    solution: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)

    # Relationships
    health_logs: Mapped[List["PlantHealthLog"]] = relationship("PlantHealthLog", back_populates="disease")
