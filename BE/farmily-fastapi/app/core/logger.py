"""
로그 설정 (Loguru)
"""
import sys
from loguru import logger

from app.core.config import settings

# 기본 로거 제거 후 커스텀 설정
logger.remove()

# 콘솔 출력 설정
logger.add(
    sys.stdout,
    colorize=True,
    format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> | <level>{level: <8}</level> | <cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> - <level>{message}</level>",
    level="DEBUG" if settings.DEBUG else "INFO"
)

# 파일 로깅 (선택사항)
# logger.add(
#     "logs/farmily_{time:YYYY-MM-DD}.log",
#     rotation="1 day",
#     retention="7 days",
#     compression="zip",
#     level="INFO"
# )

__all__ = ["logger"]
