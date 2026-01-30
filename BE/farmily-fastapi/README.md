# 🌱 Farmily FastAPI Backbone

스마트팜 관리를 위한 FastAPI 백엔드 프로젝트 뼈대(Boilerplate)입니다.
비즈니스 로직은 비어 있으며, `boilerplate` 가이드에 따라 구현을 채워넣을 수 있도록 구조만 잡혀 있습니다.

## 🚀 빠른 시작

### 1. 가상환경 생성 및 활성화

```bash
# Windows
python -m venv venv
venv\Scripts\activate

# macOS/Linux
python -m venv venv
source venv/bin/activate
```

### 2. 의존성 설치

```bash
pip install -r requirements.txt
```

### 3. 환경 변수 설정

```bash
cp .env.example .env
# .env 파일 내 설정 수정
```

### 4. 서버 실행

```bash
uvicorn app.main:app --reload
```