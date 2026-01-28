"""
Diaries - 데이터베이스 모델
"""
from sqlalchemy import Column, Integer, String, Text, ForeignKey, TIMESTAMP, BigInteger
from sqlalchemy.sql import func
from app.core.database import Base
try:
    from app.modules.plants.models import Plant
except ImportError:
    pass # 순환 참조 방지 또는 models 모듈 초기화 시점 차이 고려 (단순 등록용)

class PlantDiary(Base):
    __tablename__ = "plant_diary"

    id = Column(BigInteger, primary_key=True, index=True)
    plant_id = Column(BigInteger, ForeignKey("plant.id", ondelete="CASCADE"), nullable=False)
    title = Column(String(200), nullable=True) # 제목은 API 스펙에서 빠졌지만 DB에는 있음
    content = Column(Text, nullable=True)
    image_url = Column(Text, nullable=True)
    recorded_at = Column(TIMESTAMP(timezone=True), default=func.now())
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())
