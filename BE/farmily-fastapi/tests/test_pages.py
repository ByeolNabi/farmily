
import pytest

@pytest.mark.asyncio
async def test_get_mypage(client):
    """마이페이지 정보 조회 테스트"""
    response = await client.get("/api/v1/pages/mypage")
    assert response.status_code == 200
    
    data = response.json()
    
    # 1. Plant Info 검증
    assert "plant_info" in data
    assert data["plant_info"]["nickname"] == "밤티두리"
    assert data["plant_info"]["species"] == "몬스테라"
    
    # 2. Activity Stats 검증
    assert "activity_stats" in data
    assert data["activity_stats"]["petting_count"] == 45
    
    # 3. Achievements 검증
    assert "achievements" in data
    assert len(data["achievements"]["earned_badges"]) > 0
    assert data["achievements"]["earned_badges"][0]["icon"].startswith("badge_")
