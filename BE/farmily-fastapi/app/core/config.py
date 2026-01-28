"""
환경 변수 관리 (Pydantic Settings)
모든 설정은 .env 파일 또는 환경 변수에서 로드됩니다.
"""
from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    """애플리케이션 설정"""
    
    # Application
    APP_NAME: str = "Farmily API"
    APP_ENV: str = "development"
    DEBUG: bool = True
    
    # Database Mode
    USE_MOCK_DB: bool = True
    
    # JWT Auth
    SECRET_KEY: str = "farmily_secret_key_farmily_secret_key_1234"
    AUTH_ENABLED: bool = True
    
    # Database
    DATABASE_URL: str = "sqlite+aiosqlite:///./farmily.db"
    
    # MQTT
    MQTT_HOST: str = "localhost"
    MQTT_PORT: int = 1883
    MQTT_USERNAME: str = ""
    MQTT_PASSWORD: str = ""
    
    # AI Service
    AI_API_URL: str = "http://localhost:8001/api/v1"
    AI_API_KEY: str = ""
    
    # Server
    HOST: str = "0.0.0.0"
    PORT: int = 8000
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = True


@lru_cache()
def get_settings() -> Settings:
    """설정 객체 캐싱 (싱글톤 패턴)"""
    return Settings()


settings = get_settings()
