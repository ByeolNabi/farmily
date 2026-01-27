# 📘 Farmily-FastAPI 개발 가이드

이 문서는 Farmily 백엔드 프로젝트의 아키텍처와 개발 패턴을 설명합니다.

---

## 🏗️ 아키텍처 개요 (Repository Pattern & DI)

이 프로젝트는 **Repository Pattern**과 **Dependency Injection (DI)**을 사용하여 비즈니스 로직과 데이터 접근 계층을 분리했습니다.
가장 큰 특징은 **설정 하나로 Mock 데이터 모드와 실제 DB 모드를 전환**할 수 있다는 점입니다.

### 핵심 구조

1. **Interface (`repository/interface.py`)**: 데이터 접근 메서드의 규격을 정의합니다.
2. **Implementations**:
   - `MockRepository` (`repository/mock.py`): 메모리 상의 더미 데이터를 반환합니다.
   - `SQLRepository` (`repository/sql.py`): 실제 DB(SQLAlchemy)를 통해 데이터를 조회합니다.
3. **Dependency Injection (`dependencies.py`)**: 환경 변수에 따라 적절한 구현체를 주입합니다.

---

## ⚙️ DB 모드 전환 방법

`.env` 파일의 `USE_MOCK_DB` 값을 변경하여 모드를 전환합니다.

### 1. Mock 모드 (개발 초기, 프론트엔드 협업용)
DB 없이 빠르게 API를 띄우고 싶을 때 사용합니다.

```ini
# .env
USE_MOCK_DB=true
```

### 2. Real DB 모드 (통합 테스트, 배포용)
실제 데이터베이스와 연결할 때 사용합니다.

```ini
# .env
USE_MOCK_DB=false
DATABASE_URL=postgresql+asyncpg://...
```

---

## 🚀 새로운 API 개발 시 따라야 할 패턴

새로운 모듈(예: `animals`)을 추가할 때 다음 단계를 따라주세요.

### 1. Repository Interface 정의
`app/modules/animals/repository/interface.py`에 추상 클래스를 만듭니다.

```python
class AnimalRepositoryInterface(ABC):
    @abstractmethod
    async def get_animal(self, id: int): pass
```

### 2. Mock & SQL 구현체 작성
- `repository/mock.py`: 더미 데이터를 반환하도록 구현
- `repository/sql.py`: 실제 쿼리를 수행하도록 구현

### 3. Dependency Factory 작성
`app/modules/animals/dependencies.py`에 주입 로직을 작성합니다.

```python
def get_animal_repo(db: AsyncSession = Depends(get_db)) -> AnimalRepositoryInterface:
    if settings.USE_MOCK_DB:
        return MockAnimalRepository()
    return SQLAnimalRepository(db)
```

### 4. Service에 주입
Service는 구체적인 구현체 대신 **Interface**에 의존해야 합니다.

```python
class AnimalService:
    def __init__(self, repo: AnimalRepositoryInterface):
        self.repo = repo
```

### 5. Router에서 연결
FastAPI의 `Depends`를 사용하여 Repository를 주입받아 Service를 생성합니다.

```python
@router.get("/")
def get_animals(repo: AnimalRepositoryInterface = Depends(get_animal_repo)):
    service = AnimalService(repo)
    return service.get_all()
```

---

이 패턴을 유지하면 **코드 수정 없이** 환경 설정만으로 데이터 소스를 유연하게 변경할 수 있습니다.
