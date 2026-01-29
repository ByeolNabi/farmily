from datetime import datetime
from typing import Optional, List, TYPE_CHECKING

from sqlalchemy import BigInteger, String, Text, Integer, Numeric, ForeignKey
from sqlalchemy.sql import func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base

if TYPE_CHECKING:
    from app.models.plant import Plant
    from app.models.reference import RefAchievement, RefPlantDisease


class PlantSensorLog(Base):
    """식물 센서 데이터 로그"""
    
    __tablename__ = "plant_sensor_log"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False
    )
    temperature: Mapped[Optional[float]] = mapped_column(Numeric(5, 2), nullable=True)
    humidity: Mapped[Optional[float]] = mapped_column(Numeric(5, 2), nullable=True)
    soil_moisture: Mapped[Optional[float]] = mapped_column(Numeric(5, 2), nullable=True)
    light_level: Mapped[Optional[float]] = mapped_column(Numeric(7, 2), nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant: Mapped["Plant"] = relationship("Plant", back_populates="sensor_logs")
    
    def __repr__(self) -> str:
        return f"<PlantSensorLog(id={self.id}, plant_id={self.plant_id})>"


class PlantDiary(Base):
    """식물 성장 일기"""
    
    __tablename__ = "plant_diary"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False
    )
    title: Mapped[Optional[str]] = mapped_column(String(200), nullable=True)
    content: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant: Mapped["Plant"] = relationship("Plant", back_populates="diaries")
    
    def __repr__(self) -> str:
        return f"<PlantDiary(id={self.id}, title={self.title})>"


class PlantActivityLog(Base):
    """[user-plant] 상호작용 로그들"""
    
    __tablename__ = "plant_activity_log"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False
    )
    type: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant: Mapped["Plant"] = relationship("Plant", back_populates="activity_logs")
    
    def __repr__(self) -> str:
        return f"<PlantActivityLog(id={self.id}, type={self.type})>"


class PlantActivityStats(Base):
    """[user-plant] 상호작용 총합"""
    
    __tablename__ = "plant_activity_stats"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False
    )
    activity_type: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    total_count: Mapped[int] = mapped_column(Integer, default=0, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant: Mapped["Plant"] = relationship("Plant", back_populates="activity_stats")
    
    def __repr__(self) -> str:
        return f"<PlantActivityStats(id={self.id}, activity_type={self.activity_type})>"


class PlantAchievement(Base):
    """유저가 이 식물과 달성한 도전과제들"""
    
    __tablename__ = "plant_achievement"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False
    )
    ref_achievement_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("ref_achievement.id"), nullable=False
    )
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant: Mapped["Plant"] = relationship("Plant", back_populates="achievements")
    achievement: Mapped["RefAchievement"] = relationship(
        "RefAchievement", back_populates="plant_achievements"
    )
    
    def __repr__(self) -> str:
        return f"<PlantAchievement(id={self.id}, plant_id={self.plant_id})>"


class PlantTimelapse(Base):
    """식물 타임랩스 사진들 저장소"""
    
    __tablename__ = "plant_timelapse"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False
    )
    image_url: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    plant: Mapped["Plant"] = relationship("Plant", back_populates="timelapses")
    health_logs: Mapped[List["PlantHealthLog"]] = relationship(
        "PlantHealthLog", back_populates="timelapse", cascade="all, delete-orphan"
    )
    
    def __repr__(self) -> str:
        return f"<PlantTimelapse(id={self.id}, plant_id={self.plant_id})>"


class PlantHealthLog(Base):
    """식물에게 관측된 질병들"""
    
    __tablename__ = "plant_health_logs"
    
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    plant_timelapse_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("plant_timelapse.id", ondelete="CASCADE"), nullable=False
    )
    ref_plant_disease_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("ref_plant_disease.id"), nullable=False
    )
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    
    # Relationships
    timelapse: Mapped["PlantTimelapse"] = relationship("PlantTimelapse", back_populates="health_logs")
    disease: Mapped["RefPlantDisease"] = relationship("RefPlantDisease", back_populates="health_logs")
    
    def __repr__(self) -> str:
        return f"<PlantHealthLog(id={self.id}, disease_id={self.ref_plant_disease_id})>"
