# [API] 타임랩스(Timelapse) CRUD 명세

## 설명

서버에서 정해진 시간에 자동으로 촬영된 고정 위치 사진들의 목록을 가져옵니다. 이 사진들을 안드로이드 앱에서 짧은 간격으로 교체하여 보여줌으로써 식물이 자라는 과정을 시각화합니다.

> 🌎 **안드로이드(Kotlin) 팁**: 100장 이상의 이미지를 연속으로 보여줄 때는 `Glide`나 `Coil`의 `preload()` 기능을 사용하여 다음 프레임 이미지를 미리 메모리에 올려두는 것이 끊김 없는 재생의 핵심입니다!
> 
> **FastAPI 서버 팁**: 이미지 리스트가 많을 경우, 응답 속도를 위해 필요한 필드(`url`, `order`)만 골라내는 전용 DTO를 사용하세요.

---

## 1. [GET] 타임랩스 사진 목록 조회 (Read)

### 1. Request 명세

- **Endpoint:** `GET /api/v1/timelapse`
- **Query Parameter:**

| **Key** | **타입** | **필수** | **설명** | **예시** |
| --- | --- | --- | --- | --- |
| **plant_id** | Int | X | 특정 식물의 타임랩스 필터링 | `1` |

서버에 저장된 타임랩스용 사진들을 `taken_at` 기준 오름차순(과거→현재)으로 반환합니다.

### 2. Response 명세 (성공: 200 OK)

| **Key** | **타입** | **필수** | **Null** | **설명** | **예시** |
| --- | --- | --- | --- | --- | --- |
| **total_frames** | Int | V | N | 전체 타임랩스 사진 개수 | 120 |
| **photos** | Array | V | N | 타임랩스 사진 상세 리스트 | `[]` |
| └ **frame_no** | Int | V | N | 재생 순서 (0부터 시작) | 1 |
| └ **image_url** | String | V | N | 타임랩스용 이미지 경로 | `"http://.../tl_001.jpg"` |
| └ **taken_at** | String | V | N | 사진이 실제 촬영된 시간 (ISO 8601) | `"2026-01-01T09:00:00Z"` |

### 3. 에러 코드 명세

| **Status** | **error_code** | **message (응답 메시지)** | **발생 시나리오** |
| --- | --- | --- | --- |
| **401** | `AUTH_TOKEN_INVALID` | "인증 정보가 유효하지 않습니다." | 토큰이 만료되었거나 누락된 경우 |
| **404** | `TIMELAPSE_NOT_FOUND` | "재생 가능한 타임랩스 데이터가 없습니다." | 촬영된 사진이 한 장도 없는 경우 |
| **500** | `SERVER_ERROR` | "데이터를 불러오는 중 서버 오류가 발생했습니다." | DB 연결 문제 또는 파일 서버 응답 실패 시 |

---

## 2. [POST] 타임랩스 사진 업로드 (Create)

### 1. Request 명세

- **Endpoint:** `POST /api/v1/timelapse`
- **Content-Type:** `multipart/form-data`

> ✅ 자동 촬영 시스템이나 관리자가 수동으로 사진을 업로드할 때 사용합니다.

| **Key** | **타입** | **필수** | **설명** | **예시** |
| --- | --- | --- | --- | --- |
| **image** | File | V | 타임랩스 사진 파일 (Binary Data) | `timelapse_001.jpg` |
| **taken_at** | String | V | 촬영 시점 (ISO 8601, DateTime) | `"2026-01-01T09:00:00Z"` |
| **plant_id** | Int | X | 연결할 식물 ID (선택) | `1` |

### 2. Response 명세 (성공: 201 Created)

| **Key** | **타입** | **설명** | **예시** |
| --- | --- | --- | --- |
| **photo_id** | Int | 생성된 사진의 고유 ID | 6 |
| **frame_no** | Int | 현재 재생 순서 | 5 |
| **image_url** | String | 저장된 이미지 URL | `"http://.../uuid.jpg"` |
| **taken_at** | String | 촬영 시간 | `"2026-01-01T09:00:00Z"` |

### 3. 에러 코드 명세

| **Status** | **error_code** | **message (응답 메시지)** | **발생 시나리오** |
| --- | --- | --- | --- |
| **400** | `REQUIRED_IMAGE_MISSING` | "이미지 파일은 필수입니다." | `image` 필드가 누락된 경우 |
| **400** | `REQUIRED_DATE_MISSING` | "촬영 시간(taken_at) 정보가 없습니다." | `taken_at` 필드가 누락된 경우 |
| **413** | `FILE_LIMIT_EXCEEDED` | "이미지 파일이 너무 큽니다. (최대 10MB)" | 파일 용량 초과 |
| **500** | `FILE_SAVE_ERROR` | "이미지 저장에 실패했습니다." | 서버 내부 파일 저장 오류 |

---

## 3. [DELETE] 타임랩스 사진 삭제 (Delete)

### 1. Request 명세

- **Endpoint:** `DELETE /api/v1/timelapse/{photo_id}`
- **Path Variable:** `photo_id` (삭제할 사진의 고유 ID)

### 2. Response 명세 (성공: 204 No Content)

- **Status:** `204 No Content`
- **Body:** 없음 (삭제 성공 시 본문 없이 응답)

### 3. 에러 코드 명세

| **Status** | **error_code** | **message (응답 메시지)** | **발생 시나리오** |
| --- | --- | --- | --- |
| **404** | `PHOTO_NOT_FOUND` | "삭제할 사진을 찾을 수 없습니다." | 존재하지 않는 ID로 삭제 요청 시 |
| **500** | `INTERNAL_SERVER_ERROR` | "서버 오류로 삭제에 실패했습니다." | 파일 시스템 또는 DB 오류 |

---

## 📂 이미지 저장 방식

| 항목 | 값 |
| --- | --- |
| **저장 위치** | `{project_root}/static/images/` |
| **파일명 규칙** | UUID + 원본 확장자 (예: `a1b2c3d4.jpg`) |
| **접근 경로** | `http://{host}:{port}/static/images/{filename}` |
| **DB 저장 값** | 전체 URL 문자열 |

> ⚠️ **운영 환경 주의**: 로컬 저장은 개발용입니다. 실제 배포 시 AWS S3 등 영구 스토리지로 마이그레이션하세요.
