# FastAPI Schema Design Guidelines (Pydantic v2)

이 문서는 FastAPI 프로젝트에서 데이터 전송 객체(DTO) 역할을 하는 Pydantic Schema를 작성하는 표준 패턴과 가이드라인을 정의합니다.

## 1. 핵심 패턴: Base - Create - Update - Response

우리는 **관심사의 분리(Separation of Concerns)** 원칙에 따라 하나의 모델에 대해 최소 4개의 스키마 클래스를 구성하는 것을 표준으로 합니다.

### 구조 다이어그램
```mermaid
graph TD
    Base[SchemaBase<br/>(공통 필드)]
    Base --> Create[SchemaCreate<br/>(생성 시 필수 입력)]
    Base --> Update[SchemaUpdate<br/>(수정 시 선택 입력)]
    Base --> Response[SchemaResponse<br/>(응답용 + ID/Date)]
    Response -.->|mapped from| DBModel[SQLAlchemy DB Model]
```

### 각 스키마의 역할

| 스키마 이름 | 접미사 | 상속 관계 | 역할 및 특징 |
| :--- | :--- | :--- | :--- |
| **Base** | `Base` | `BaseModel` | **중복 제거용 부모 클래스**. `Create`와 `Response` 등에서 공통으로 사용되는 필드(예: 제목, 내용)를 정의합니다. |
| **Create** | `Create` | `Base` | **생성(POST) 요청용**. 유저가 반드시 입력해야 하는 필드를 정의합니다. 유저가 알 수 없는 정보(ID, `created_at` 등)는 제외합니다. |
| **Update** | `Update` | `BaseModel` | **수정(PATCH) 요청용**. 모든 필드가 `Optional`이어야 합니다. 변경하고자 하는 필드만 전송하기 위함입니다. (`Base`를 상속받지 않는 경우가 많음 - 필수였던 필드도 수정 시엔 선택이 되기 때문) |
| **Response** | `Response` | `Base` | **조회(GET) 응답용**. 내부 DB ID, 생성 시간, 연관 데이터 등 클라이언트에게 보여줄 최종 데이터입니다. `from_attributes=True` 설정이 필수입니다. |

## 2. 작성 예시 (Code Example)

`app/schemas/diary.py` 예시입니다.

```python
from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict, Field

# 1. Base: 공통 필드 정의
class PlantDiaryBase(BaseModel):
    title: str = Field(..., max_length=200, description="일기 제목", examples=["오늘은 몬스테라 잎이 펴졌다!"])
    content: Optional[str] = Field(None, description="일기 내용")
    image_url: Optional[str] = None

# 2. Create: 생성 시 필요한 정보
class PlantDiaryCreate(PlantDiaryBase):
    # Base의 필드를 그대로 가져오되, 추가 검증이 필요하면 여기서 오버라이딩 가능
    pass 

# 3. Update: 수정 시 필요한 정보 (모두 Optional)
class PlantDiaryUpdate(BaseModel):
    # 주의: Base를 상속받으면 title이 필수가 되므로, 별도로 정의하거나 Base 필드를 모두 Optional로 재정의해야 함
    title: Optional[str] = Field(None, max_length=200)
    content: Optional[str] = None
    image_url: Optional[str] = None

# 4. Response: 클라이언트 반환 정보
class PlantDiaryResponse(PlantDiaryBase):
    id: int
    plant_id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    # Pydantic v2 설정: ORM 객체를 Pydantic 모델로 변환 허용
    model_config = ConfigDict(from_attributes=True)
```

## 3. 이 패턴의 장점 (검토 결과)

### ✅ 유효성 (Validity)
- REST API의 `POST`(생성)와 `PATCH`(수정)의 의미론적 차이를 정확히 반영합니다.
- DB 모델과 API 모델을 분리하여 보안(ID, Password 필드 노출 방지)을 강화합니다.

### 📈 확장성 (Scalability)
- 필드가 추가될 때 `Base`에 한 번만 추가하면 `Create`, `Response`에 자동으로 반영됩니다.
- 특정 API(예: 관리자 전용 응답)가 필요할 때 `PlantDiaryResponse`를 상속받아 `PlantDiaryAdminResponse`를 쉽게 만들 수 있습니다.

### 🤝 독립성 및 협업 (Independence & Collaboration)
- **Frontend**: Swagger UI에 Request와 Response 스키마가 명확히 분리되어 표시되므로, "무엇을 보내고 무엇을 받는지"에 대한 오해를 줄입니다.
- **Backend**: DB 스키마가 변경되어도 `Response` 스키마에서 매핑 로직만 수정하면 API 계약(Contract)을 유지할 수 있어 독립적인 개발이 가능합니다.

## 4. 추가 가이드라인

### 4.1. Naming Convention
- 파일명: 도메인 단위 단수형 (예: `user.py`, `plant.py`)
- 클래스명: `[Domain][Action]` (예: `UserCreate`, `UserResponse`)

### 4.2. Update 스키마 작성 팁
`Update` 스키마 작성 시 `Base`를 상속받으면 모든 필드가 `Required` 상태로 상속되므로, `Update`는 보통 `BaseModel`을 새로 상속받아 작성하거나, 모든 필드를 다시 `Optional`로 선언해야 합니다. 실수로 `Base`를 상속받아 수정 API에서 모든 필드를 요구하는 실수가 잦으니 주의하세요.

### 4.3. 순환 참조(Circular Reference) 주의
User가 Plant를 가지고, Plant가 User를 가지는 관계에서 Response 스키마 간 순환 참조가 발생할 수 있습니다.
이 경우 `TYPE_CHECKING`을 사용하거나, `UserListResponse`와 `UserDetailResponse`처럼 용도에 따라 스키마를 분리하여 깊이(Depth)를 조절해야 합니다.
