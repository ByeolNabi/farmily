"""
Users - 데이터베이스 모델
"""
from sqlalchemy import Column, Integer, String, Text, BigInteger, TIMESTAMP
from sqlalchemy.sql import func
from app.core.database import Base

class Users(Base):
    __tablename__ = "users"

    id = Column(BigInteger, primary_key=True, index=True)
    profile_image_url = Column(Text, nullable=True)
    name = Column(String(100), nullable=True)
    email = Column(String(255), nullable=False, unique=True, index=True)
    password = Column(String(255), nullable=True)
    fcm_token = Column(Text, nullable=True)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, default=func.now())
