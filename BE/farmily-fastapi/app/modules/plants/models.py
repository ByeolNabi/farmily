"""
Plants - 데이터베이스 모델
"""
from sqlalchemy import Column, Integer, String, ForeignKey, TIMESTAMP
from sqlalchemy.sql import func
from app.core.database import Base

class Plant(Base):
    __tablename__ = "plant"

    id = Column(Integer, primary_key=True, index=True)
    users_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    device_id = Column(Integer, ForeignKey("edge_device.id", ondelete="SET NULL"), nullable=True)
    nickname = Column(String(50))
    plant_type = Column(String(50))
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())
