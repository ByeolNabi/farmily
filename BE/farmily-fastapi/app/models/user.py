from datetime import datetime
from typing import List, Optional, TYPE_CHECKING
from sqlalchemy import BigInteger, String, Text, Boolean, text, func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.core.database import Base

if TYPE_CHECKING:
    from app.models.plant import Plant

class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    email: Mapped[str] = mapped_column(String(255), unique=True, nullable=False)
    password: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    name: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    profile_image_url: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    fcm_token: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    updated_at: Mapped[datetime] = mapped_column(server_default=func.now(), nullable=False)
    is_deleted: Mapped[bool] = mapped_column(Boolean, nullable=False, server_default=text("false"))

    # Relationships
    plants: Mapped[List["Plant"]] = relationship("Plant", back_populates="user")
