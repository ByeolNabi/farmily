"""
Sensors - Repository
"""
from sqlalchemy.ext.asyncio import AsyncSession

class SensorRepository:
    """
    [Step 4] 복잡한 통계 쿼리 분리 (DB 접근 계층)
    """
    def __init__(self, session: AsyncSession):
        self.session = session
    
    async def get_statistics(self):
        # TODO: 평균, 최대/최소값 등 통계 쿼리 작성
        pass
