"""
Pages - Mock Repository implementation
"""
from typing import Optional, Dict, Any
from app.modules.pages.repository.interface import PageRepositoryInterface

# 사용자 명세에 따른 더미 데이터
_MOCK_MYPAGE_DATA = {
    "plant_info": {
        "nickname": "밤티두리",
        "species": "몬스테라",
        "character_url": "https://api.server.com/static/img.png",
        "start_date": "2025-10-20",
        "days_met": 97,
        "bond_warmth": 43.5
    },
    "activity_stats": {
        "petting_count": 45,
        "watering_count": 12,
        "talking_count": 30,
        "praising_count": 22,
        "diary_count": 5
    },
    "achievements": {
        "earned_badges": [
            {"id": 11, "name": "만난지 1일", "icon": "badge_1days"},
            {"id": 12, "name": "만난지 10일", "icon": "badge_10days"},
            {"id": 13, "name": "만난지 50일", "icon": "badge_50days"}
        ],
        "unearned_badges": [
            {"id": 14, "name": "만난지 100일", "icon": "badge_100days"}
        ]
    }
}

class MockPageRepository(PageRepositoryInterface):
    """메모리 상의 더미 데이터를 사용하는 리포지토리"""
    
    async def get_mypage_info(self, user_id: int) -> Optional[Dict[str, Any]]:
        # Mock 모드에서는 user_id에 상관없이 항상 동일한 예시 데이터 반환
        return _MOCK_MYPAGE_DATA
