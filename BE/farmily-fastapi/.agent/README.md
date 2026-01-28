# 🤖 AI Agent Guidelines

이 디렉토리는 AI 에이전트(Assistant)가 프로젝트를 이해하고 일관된 코드를 작성하기 위한 지침서들을 보관합니다.

## 📂 구조

### 1. `rules/` (규칙 및 아키텍처)
- **`architecture.md`**: 프로젝트의 핵심 아키텍처 (Repository Pattern, Layered Architecture 등).
- **`conventions.md`**: 코드 스타일, 변수 명명 규칙 등.

### 2. `workflows/` (작업 절차)
- **`deployment.md`**: 배포 절차.
- **`testing.md`**: 테스트 작성 및 실행 가이드.

### 3. `knowledge/` (도메인 지식)
- **`domain.md`**: 비즈니스 로직, 용어 정의 등.

---

## 💡 AI에게 지시하는 법

이 프로젝트에서 작업할 때는 다음과 같이 요청하면 AI가 더 정확하게 동작합니다.

- **"아키텍처 가이드를 참고해서 구현해줘"**
- **"에이전트 룰에 맞춰서 리팩토링해줘"**
- **"배포 워크플로우를 따라줘"** (workflows 폴더 참고 시)

에이전트는 이 폴더(`/.agent`)의 내용을 우선적으로 확인하여 작업합니다.
