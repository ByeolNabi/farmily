"""
Plants - 데이터베이스 모델
"""
from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, TIMESTAMP, Text, BigInteger
from sqlalchemy.sql import func
from sqlalchemy.dialects.postgresql import INT4RANGE
from geoalchemy2 import Geometry
from app.core.database import Base

# === Reference Tables ===

class RefPlantSpecies(Base):
    __tablename__ = "ref_plant_species"

    id = Column(BigInteger, primary_key=True, index=True)
    name = Column(String(100))
    image_url = Column(Text)
    temp_target = Column(Integer)
    temp_range = Column(INT4RANGE)
    humid_target = Column(Integer)
    humid_range = Column(INT4RANGE)
    soil_target = Column(Integer)
    soil_range = Column(INT4RANGE)
    light_intensity = Column(Integer)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())

class RefPlantDisease(Base):
    __tablename__ = "ref_plant_disease"

    id = Column(BigInteger, primary_key=True, index=True)
    name = Column(String(100))
    symptoms = Column(Text)
    cure_desc = Column(Text)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())

class RefAchievement(Base):
    __tablename__ = "ref_achievement"

    id = Column(BigInteger, primary_key=True, index=True)
    name = Column(String(100))
    icon = Column(Text)
    description = Column(Text)
    action_type = Column(String(50))
    action_count = Column(Integer)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())


# === Main Plant Table ===

class Plant(Base):
    __tablename__ = "plant"

    id = Column(BigInteger, primary_key=True, index=True)
    users_id = Column(BigInteger, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    ref_plant_species_id = Column(BigInteger, ForeignKey("ref_plant_species.id"), nullable=False, index=True)
    
    # DB 스키마에 없는 컬럼 주석 처리
    # device_id = Column(Integer, ForeignKey("edge_device.id", ondelete="SET NULL"), nullable=True)
    
    profile_image_url = Column(Text)
    is_active = Column(Boolean, default=True)
    health_status = Column(String(50))
    signed_at = Column(TIMESTAMP(timezone=True))
    love_temperature = Column(Integer, default=0, nullable=False)
    
    # PostGIS Point (SRID 4326)
    station_point = Column(Geometry(geometry_type='POINT', srid=4326))
    
    nickname = Column(String(50))
    started_at = Column(TIMESTAMP(timezone=True))
    ended_at = Column(TIMESTAMP(timezone=True))
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())


# === Logs & Stats ===

class PlantAchievement(Base):
    __tablename__ = "plant_achievement"
    
    id = Column(BigInteger, primary_key=True, index=True)
    plant_id = Column(BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False)
    ref_achievement_id = Column(BigInteger, ForeignKey("ref_achievement.id"), nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())

class PlantActivityLog(Base):
    __tablename__ = "plant_activity_log"
    
    id = Column(BigInteger, primary_key=True, index=True)
    plant_id = Column(BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False)
    type = Column(String(50)) # WATER, LIGHT_ON, TALK...
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())

class PlantActivityStats(Base):
    __tablename__ = "plant_activity_stats"
    
    id = Column(BigInteger, primary_key=True, index=True)
    plant_id = Column(BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False)
    activity_type = Column(String(50))
    total_count = Column(Integer, default=0)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())
