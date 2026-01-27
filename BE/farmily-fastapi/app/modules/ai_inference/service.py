"""
AI Inference - 서비스 로직
"""
from app.core.logger import logger
from app.modules.ai_inference.schemas import AIAnalysisRequest
from app.infra.ai_client import ai_client

class AIInferenceService:
    """
    [Step 5] AI 기능 (API Wrapper)
    - 외부 AI API 호출 및 결과 처리
    """
    
    async def analyze(self, request: AIAnalysisRequest):
        # TODO: infra.ai_client를 통해 외부 AI 엔진에 분석 요청
        logger.info("Requesting AI analysis")
        # await ai_client.analyze_image(...)
        pass

# 서비스 인스턴스
ai_inference_service = AIInferenceService()
