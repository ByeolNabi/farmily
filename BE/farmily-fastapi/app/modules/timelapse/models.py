"""
Timelapse & Health Logs - 데이터베이스 모델
"""
from sqlalchemy import Column, Integer, String, Text, ForeignKey, TIMESTAMP, BigInteger
from sqlalchemy.sql import func
from app.core.database import Base

class PlantTimelapse(Base):
    __tablename__ = "plant_timelapse"

    id = Column(BigInteger, primary_key=True, index=True)
    plant_id = Column(BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False, index=True)
    image_url = Column(Text)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())

class PlantHealthLogs(Base):
    __tablename__ = "plant_health_logs"

    id = Column(BigInteger, primary_key=True, index=True)
    plant_timelapse_id = Column(BigInteger, ForeignKey("plant_timelapse.id", ondelete="CASCADE"), nullable=False)
    ref_plant_disease_id = Column(BigInteger, ForeignKey("ref_plant_disease.id"), nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())
