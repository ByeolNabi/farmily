"""
API 테스트 설정
"""
import pytest
from httpx import AsyncClient, ASGITransport
from main import app


@pytest.fixture
async def client():
    """비동기 테스트 클라이언트"""
    async with AsyncClient(
        transport=ASGITransport(app=app),
        base_url="http://test"
    ) as ac:
        yield ac


@pytest.mark.asyncio
async def test_root(client):
    """루트 엔드포인트 테스트"""
    response = await client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert "Farmily" in data["message"]


@pytest.mark.asyncio
async def test_health_check(client):
    """헬스체크 테스트"""
    response = await client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert "services" in data


@pytest.mark.asyncio
async def test_get_devices(client):
    """장비 목록 조회 테스트"""
    response = await client.get("/api/v1/devices")
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "data" in data
    assert isinstance(data["data"], list)


@pytest.mark.asyncio
async def test_get_sensor_data(client):
    """센서 데이터 조회 테스트"""
    response = await client.get("/api/v1/sensors")
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "data" in data


@pytest.mark.asyncio
async def test_ai_health(client):
    """AI 서비스 상태 테스트"""
    response = await client.get("/api/v1/ai/health")
    assert response.status_code == 200
    data = response.json()
    assert "status" in data
