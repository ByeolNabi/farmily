"""
외부 AI API 호출 래퍼 (HTTPX)
"""
from typing import Any, Optional
import httpx

from app.core.config import settings
from app.core.logger import logger
from app.core.exceptions import AIServiceException


class AIClient:
    """비동기 AI 서비스 클라이언트"""
    
    def __init__(self):
        self._base_url = settings.AI_API_URL
        self._api_key = settings.AI_API_KEY
        self._timeout = 30.0
    
    async def analyze_image(self, image_url: str) -> dict[str, Any]:
        """이미지 분석 요청"""
        try:
            async with httpx.AsyncClient(timeout=self._timeout) as client:
                response = await client.post(
                    f"{self._base_url}/analyze",
                    json={"image_url": image_url},
                    headers=self._get_headers()
                )
                response.raise_for_status()
                return response.json()
        except httpx.HTTPError as e:
            logger.error(f"AI service error: {e}")
            raise AIServiceException(f"AI analysis failed: {str(e)}")
    
    async def predict(self, data: dict[str, Any]) -> dict[str, Any]:
        """예측 요청"""
        try:
            async with httpx.AsyncClient(timeout=self._timeout) as client:
                response = await client.post(
                    f"{self._base_url}/predict",
                    json=data,
                    headers=self._get_headers()
                )
                response.raise_for_status()
                return response.json()
        except httpx.HTTPError as e:
            logger.error(f"AI service error: {e}")
            raise AIServiceException(f"AI prediction failed: {str(e)}")
    
    async def health_check(self) -> bool:
        """AI 서비스 상태 확인"""
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(f"{self._base_url}/health")
                return response.status_code == 200
        except Exception:
            return False
    
    def _get_headers(self) -> dict[str, str]:
        """요청 헤더 생성"""
        headers = {"Content-Type": "application/json"}
        if self._api_key:
            headers["Authorization"] = f"Bearer {self._api_key}"
        return headers


# 싱글톤 인스턴스
ai_client = AIClient()
