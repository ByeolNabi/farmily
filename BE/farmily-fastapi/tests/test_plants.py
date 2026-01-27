import pytest

@pytest.mark.asyncio
async def test_get_plants(client):
    """식물 목록 조회 테스트"""
    response = await client.get("/api/v1/plants")
    assert response.status_code == 200
    data = response.json()
    assert data["total_count"] >= 0
    assert isinstance(data["plants"], list)
    if data["plants"]:
        plant = data["plants"][0]
        assert "id" in plant
        assert "name" in plant
        assert "thumbnail" in plant

@pytest.mark.asyncio
async def test_get_plant_detail(client):
    """식물 상세 조회 테스트"""
    # 존재하는 ID (Mock Data: 1)
    response = await client.get("/api/v1/plants/1")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == 1
    assert data["name"] == "상추"
    assert "soil_moisture" in data

@pytest.mark.asyncio
async def test_get_plant_not_found(client):
    """존재하지 않는 식물 조회 테스트"""
    response = await client.get("/api/v1/plants/999")
    assert response.status_code == 404
    data = response.json()
    assert data["error_code"] == "PLANT_NOT_FOUND"
