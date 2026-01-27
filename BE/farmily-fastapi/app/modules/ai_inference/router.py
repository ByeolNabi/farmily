"""
AI Inference - API 라우터
"""
from fastapi import APIRouter
from app.modules.ai_inference.schemas import AIAnalysisRequest
from app.modules.ai_inference.service import ai_inference_service

router = APIRouter()

@router.post("/analyze")
async def analyze_image(request: AIAnalysisRequest):
    """
    [Step 5] AI 분석 요청 엔드포인트
    """
    await ai_inference_service.analyze(request)
    return {"status": "ok", "message": "Not implemented yet"}
