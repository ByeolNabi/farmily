# 🔐 JWT Authentication Design

## 1. Overview

이 문서는 **반려식물 일기(Growth Diary) API**에서 사용할 JWT(JSON Web Token) 인증 구조를 정의합니다.
핵심 목표는 **사용자가 자신의 데이터에만 접근**하도록 보장하는 것입니다.

---

## 2. JWT Token Types

| Token Type | Purpose | Expiry |
|------------|---------|--------|
| **Access Token** | API 요청 인증에 사용 | 15분 ~ 1시간 (권장: 30분) |
| **Refresh Token** | Access Token 갱신용 | 7일 ~ 30일 (권장: 14일) |

---

## 3. Access Token Payload (Claims)

```json
{
  "sub": "user_123",
  "user_id": 123,
  "username": "plant_lover",
  "role": "user",
  "iat": 1706345678,
  "exp": 1706347478
}
```

### Field Descriptions

| Claim | Type | Required | Description |
|-------|------|----------|-------------|
| `sub` | String | ✅ | Subject - 사용자 고유 식별자 (문자열 형태) |
| `user_id` | Int | ✅ | **DB User PK** - 일기 소유권 확인에 사용 |
| `iat` | Int | ✅ | Issued At - 토큰 발급 시간 (Unix timestamp) |
| `exp` | Int | ✅ | Expiration - 토큰 만료 시간 (Unix timestamp) |

---

## 4. Refresh Token Payload

```json
{
  "sub": "user_123",
  "user_id": 123,
  "type": "refresh",
  "iat": 1706345678,
  "exp": 1707555078
}
```

> [!NOTE]
> Refresh Token은 Access Token 재발급에만 사용되며, API 요청 인증에는 사용하지 않습니다.

---