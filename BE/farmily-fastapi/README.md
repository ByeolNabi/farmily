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
uvicorn main:app --reload
```

## 📂 프로젝트 구조

이 프로젝트는 **Layered Architecture**를 따릅니다.

```
farmily-fastapi/
├── main.py                 # 앱 진입점 (Lifespan, 라우터 등록)
├── requirements.txt        # 의존성 패키지
├── app/
│   ├── core/              # [설정] Config, DB, Logger
│   ├── infra/             # [인프라] 외부 통신 (MQTT, AI Class)
│   ├── modules/           # [도메인] 비즈니스 로직 (구현 필요)
│   │   ├── edge_devices/  # 장비 모듈 템플릿
│   │   ├── sensors/       # 센서 모듈 템플릿
│   │   └── ai_inference/  # AI 모듈 템플릿
│   └── common/            # [공통] 유틸리티
└── tests/
```

## � 구현 가이드 (To-Do)

각 모듈(`app/modules/*`) 내부의 파일들은 현재 **Skeleton Code** 상태입니다.
`boilerplate` 문서의 지침(Step 3 ~ Step 5)에 따라 다음 내용을 구현하세요.

1. **Schemas (`schemas.py`)**: 요청/응답에 필요한 Pydantic 모델 정의
2. **Models (`models.py`)**: DB 테이블 정의 (SQLAlchemy)
3. **Service (`service.py`)**: 비즈니스 로직 구현
4. **Router (`router.py`)**: API 엔드포인트 구현 및 연결

---

## 🔌 API 명세 (예시)

서버 실행 후 `http://localhost:8000/docs`에서 Swagger UI를 확인할 수 있습니다.
현재는 뼈대만 잡혀 있는 상태입니다.

- **POST** `/api/v1/devices/{device_id}/command`
- **GET** `/api/v1/sensors/stats`
- **POST** `/api/v1/ai/analyze`
