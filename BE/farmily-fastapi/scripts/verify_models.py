import sys
import os

# 프로젝트 루트 경로 추가
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

try:
    print("Importing models...")
    from app.modules.users.models import Users
    print("[OK] Users model imported")
    
    from app.modules.plants.models import (
        Plant, RefPlantSpecies, RefPlantDisease, RefAchievement,
        PlantAchievement, PlantActivityLog, PlantActivityStats
    )
    print("[OK] Plant & Ref models imported")
    
    from app.modules.timelapse.models import PlantTimelapse, PlantHealthLogs
    print("[OK] Timelapse models imported")
    
    from app.modules.diaries.models import PlantDiary
    import app.modules.diaries.repository.sql
    print("[OK] Diary models imported")
    
    print("\n[SUCCESS] All models verified successfully!")
    
except Exception as e:
    print(f"\n[FAIL] Verification Failed: {e}")
    import traceback
    traceback.print_exc()
