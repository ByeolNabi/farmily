"""
API 테스트 설정
"""
import pytest

@pytest.mark.asyncio
async def test_root(client):
    """루트 엔드포인트 테스트"""
    response = await client.get("/")
    # 현재 루트는 200 OK를 반환해야 함
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"

@pytest.mark.asyncio
async def test_health_check(client):
    """헬스체크 테스트"""
    response = await client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
