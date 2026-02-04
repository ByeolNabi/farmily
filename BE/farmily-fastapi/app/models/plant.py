from datetime import datetime
from typing import Optional, List, TYPE_CHECKING
from decimal import Decimal
from sqlalchemy import BigInteger, String, Text, Integer, Boolean, ForeignKey, text, func, Numeric
from sqlalchemy.orm import Mapped, mapped_column, relationship
from geoalchemy2 import Geometry
from app.core.database import Base

if TYPE_CHECKING:
    from app.models.user import User
    from app.models.reference import RefPlantSpecies
    from app.models.plant_log import (
        PlantSensorLog,
        PlantDiary,
        PlantActivityLog,
        PlantActivityCounts,
        PlantAchievement,
        PlantTimelapse,
    )

class Plant(Base):
    """유저별 반려 식물 정보"""
    
    __tablename__ = "plant"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    users_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("users.id", ondelete="CASCADE"), nullable=False
    )
    ref_plant_species_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("ref_plant_species.id"), nullable=False
    )
    nickname: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    profile_image_url: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    health_status: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    health_checked_at: Mapped[Optional[datetime]] = mapped_column(nullable=True)
    love_temperature: Mapped[Decimal] = mapped_column(Numeric(5, 2), server_default=text("0"), nullable=False)
    is_active: Mapped[bool] = mapped_column(Boolean, server_default=text("true"), nullable=True)
    # Using String to match Spring Boot's behavior (WKT format or "lat,lon")
    station_point: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    started_at: Mapped[Optional[datetime]] = mapped_column(nullable=True)
    ended_at: Mapped[Optional[datetime]] = mapped_column(nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    user: Mapped["User"] = relationship("User", back_populates="plants")
    species: Mapped["RefPlantSpecies"] = relationship("RefPlantSpecies", back_populates="plants")
    
    sensor_logs: Mapped[List["PlantSensorLog"]] = relationship(
        "PlantSensorLog", back_populates="plant", cascade="all, delete-orphan"
    )
    diaries: Mapped[List["PlantDiary"]] = relationship(
        "PlantDiary", back_populates="plant", cascade="all, delete-orphan"
    )
    activity_logs: Mapped[List["PlantActivityLog"]] = relationship(
        "PlantActivityLog", back_populates="plant", cascade="all, delete-orphan"
    )
    activity_stats: Mapped[List["PlantActivityCounts"]] = relationship(
        "PlantActivityCounts", back_populates="plant", cascade="all, delete-orphan"
    )
    achievements: Mapped[List["PlantAchievement"]] = relationship(
        "PlantAchievement", back_populates="plant", cascade="all, delete-orphan"
    )
    timelapses: Mapped[List["PlantTimelapse"]] = relationship(
        "PlantTimelapse", back_populates="plant", cascade="all, delete-orphan"
    )
    
    def __repr__(self) -> str:
        return f"<Plant(id={self.id}, nickname={self.nickname})>"
