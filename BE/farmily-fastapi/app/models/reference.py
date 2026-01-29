from datetime import datetime
from typing import Optional, List, TYPE_CHECKING

from sqlalchemy import BigInteger, String, Text, Integer
from sqlalchemy.sql import func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import INT4RANGE

from app.core.database import Base

if TYPE_CHECKING:
    from app.models.plant import Plant
    from app.models.plant_log import PlantAchievement, PlantHealthLog


class RefPlantSpecies(Base):
    """식물들을 키우기 위해 알아야 할 정보들"""
    
    __tablename__ = "ref_plant_species"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    image_url: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    temp_target: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    temp_range = mapped_column(INT4RANGE, nullable=True)  # PostgreSQL Range type
    humid_target: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    humid_range = mapped_column(INT4RANGE, nullable=True)
    soil_target: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    soil_range = mapped_column(INT4RANGE, nullable=True)
    light_intensity: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    
    # Relationships
    plants: Mapped[List["Plant"]] = relationship("Plant", back_populates="species")
    
    def __repr__(self) -> str:
        return f"<RefPlantSpecies(id={self.id}, name={self.name})>"


class RefAchievement(Base):
    """도전과제들"""
    
    __tablename__ = "ref_achievement"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    icon: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    action_type: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    action_count: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant_achievements: Mapped[List["PlantAchievement"]] = relationship(
        "PlantAchievement", back_populates="achievement"
    )
    
    def __repr__(self) -> str:
        return f"<RefAchievement(id={self.id}, name={self.name})>"


class RefPlantDisease(Base):
    """식물 진단 데이터"""
    
    __tablename__ = "ref_plant_disease"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)  # 병명
    symptoms: Mapped[Optional[str]] = mapped_column(Text, nullable=True)  # 증상설명
    cure_desc: Mapped[Optional[str]] = mapped_column(Text, nullable=True)  # 치료방법
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    health_logs: Mapped[List["PlantHealthLog"]] = relationship(
        "PlantHealthLog", back_populates="disease"
    )
    
    def __repr__(self) -> str:
        return f"<RefPlantDisease(id={self.id}, name={self.name})>"
