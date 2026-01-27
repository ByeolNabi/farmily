"""
전역 예외 처리 (Global Exception Handler)
"""
from fastapi import Request, status
from fastapi.responses import JSONResponse
from app.core.logger import logger


class FarmilyException(Exception):
    """Farmily API 기본 예외"""
    def __init__(self, message: str, status_code: int = 400):
        self.message = message
        self.status_code = status_code
        super().__init__(self.message)


class DeviceNotFoundException(FarmilyException):
    """장비를 찾을 수 없음"""
    def __init__(self, device_id: str):
        super().__init__(
            message=f"Device not found: {device_id}",
            status_code=status.HTTP_404_NOT_FOUND
        )


class SensorDataException(FarmilyException):
    """센서 데이터 오류"""
    def __init__(self, message: str):
        super().__init__(
            message=message,
            status_code=status.HTTP_400_BAD_REQUEST
        )


class AIServiceException(FarmilyException):
    """AI 서비스 오류"""
    def __init__(self, message: str):
        super().__init__(
            message=message,
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE
        )


async def farmily_exception_handler(request: Request, exc: FarmilyException):
    """Farmily 예외 핸들러"""
    logger.error(f"FarmilyException: {exc.message}")
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "success": False,
            "error": {
                "message": exc.message,
                "code": exc.status_code
            }
        }
    )


async def general_exception_handler(request: Request, exc: Exception):
    """일반 예외 핸들러"""
    logger.exception(f"Unexpected error: {exc}")
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={
            "success": False,
            "error": {
                "message": "Internal server error",
                "code": 500
            }
        }
    )
