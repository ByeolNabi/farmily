import asyncio
import os
import sys

# 프로젝트 루트 디렉토리를 path에 추가
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from sqlalchemy.ext.asyncio import create_async_engine
from sqlalchemy import text
from dotenv import load_dotenv

# .env 로드
load_dotenv()

async def check_connection():
    database_url = os.getenv("DATABASE_URL")
    
    if not database_url:
        print("[!] DATABASE_URL is not set in .env")
        return

    print(f"[*] Connecting to database...")
    
    if "YOUR_USER" in database_url:
        print("[!] Please update .env with your actual database credentials.")
        return

    try:
        engine = create_async_engine(database_url)
        async with engine.connect() as conn:
            result = await conn.execute(text("SELECT 1"))
            print("[OK] Database connection successful!")
            print(f"     Result: {result.scalar()}")
    except Exception as e:
        print(f"[ERROR] Connection failed: {e}")

if __name__ == "__main__":
    if sys.platform == "win32":
        asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
    asyncio.run(check_connection())
