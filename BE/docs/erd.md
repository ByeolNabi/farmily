```mermaid
erDiagram
    %% ==========================================
    %% 1. 관계 정의 (Relationships)
    %% ==========================================

    %% 사용자 중심
    USERS ||--o| STATIONS : "보유 (1:1)"
    USERS ||--o{ DEVICES : "보유 (N)"
    USERS ||--o{ PLANTS : "육성 (N)"
    USERS ||--o{ BADGES : "획득"
    USERS ||--o{ DIARIES : "작성"

    %% 하드웨어 & 식물
    DEVICES ||--o| PLANTS : "심겨짐 (1:1)"
    DEVICES ||--o{ SENSOR_LOGS : "데이터 생성"
    DEVICES ||--o{ WAYPOINTS : "지도 좌표 보유"

    %% 식물 파생 데이터
    PLANTS ||--o{ DIARIES : "일기 대상"
    PLANTS ||--o{ TIMELAPSE_IMAGES : "성장 기록"
    PLANTS ||--o{ PLEDGES : "치료 서약"
    PLANTS ||--o{ CHAT_LOGS : "대화 기록"
    PLANTS ||--o{ DISEASE_LOGS : "진단 이력"

    %% ==========================================
    %% 2. 테이블 상세 정의 (Schema)
    %% ==========================================

    %% [1] 사용자 (Users)
    USERS {
        bigint user_id PK "회원 고유 ID"
        varchar email UK "로그인 ID (이메일)"
        varchar password "비밀번호 (BCrypt)"
        varchar nickname "사용자 닉네임"
        varchar fcm_token "푸시 알림 토큰"
        timestamp created_at "가입일"
        boolean is_deleted "탈퇴 여부 (Soft Delete)"
    }

    %% [2] 스테이션 (Stations)
    STATIONS {
        bigint station_id PK "스테이션 ID"
        varchar uuid UK "기기 시리얼 (station-xxx)"
        bigint user_id FK "소유자 ID"
        boolean light_status "조명 상태 (ON/OFF)"
    }

    %% [3] 로봇 (Devices - 이동형 화분)
    DEVICES {
        bigint device_id PK "로봇 ID"
        varchar uuid UK "기기 시리얼 (robot-xxx)"
        bigint user_id FK "소유자 ID"
        varchar nickname "로봇 애칭"
    }

    %% [4] 지도 좌표 (Waypoints - PostGIS)
    WAYPOINTS {
        bigint point_id PK "좌표 ID"
        bigint device_id FK "해당 로봇"
        varchar name "장소명 (거실, 주방)"
        geometry location "공간 좌표 POINT(x, y)"
    }

    %% [5] 식물 (Plants - 핵심 엔티티)
    PLANTS {
        bigint plant_id PK "식물 ID"
        bigint user_id FK "주인 ID"
        bigint device_id FK "심겨진 로봇 ID"
        varchar species "품종 (몬스테라)"
        varchar nickname "식물 애칭"
        int optimal_light "권장 광주기 (시간)"
        float optimal_moisture "적정 습도 (%)"
        int attachment_score "현재 애착 점수"
        varchar attachment_grade "현재 애착 등급"
        varchar current_mood "현재 표정 (HAPPY, SAD)"
        int total_touch_count "누적 쓰다듬기 횟수"
        int total_water_count "누적 물주기 횟수"
        int total_diary_count "누적 일기 횟수"
        timestamp created_at "입양일 (D-Day)"
    }

    %% [6] 센서 로그 (Sensor Logs)
    SENSOR_LOGS {
        bigint log_id PK "로그 ID"
        varchar device_uuid FK "로봇 UUID (속도 최적화)"
        float temperature "온도"
        float humidity "습도"
        float light "조도"
        float soil_moisture "토양 습도"
        timestamp created_at "수집 시간"
    }

    %% [7] AI 질병 진단 로그 (Disease Logs)
    DISEASE_LOGS {
        bigint log_id PK "로그 ID"
        bigint plant_id FK "대상 식물"
        varchar disease_code "진단된 병명 코드"
        float confidence "AI 신뢰도 (0~1.0)"
        varchar image_url "진단 당시 캡처 URL"
        timestamp created_at "진단 시간"
    }

    %% [8] 치료 서약 (Pledges)
    PLEDGES {
        bigint pledge_id PK "서약 ID"
        bigint plant_id FK "대상 식물"
        varchar disease_code "면역 처리할 병명"
        timestamp created_at "서약 시작일"
        timestamp expires_at "만료일 (면역 종료)"
    }

    %% [9] 대화 기록 (Chat Logs)
    CHAT_LOGS {
        bigint chat_id PK "대화 ID"
        bigint plant_id FK "대상 식물"
        varchar sender "USER / PLANT"
        text message "대화 내용"
        timestamp created_at "전송 시간"
    }

    %% [10] 성장 일기 (Timelapse)
    TIMELAPSE_IMAGES {
        bigint image_id PK "사진 ID"
        bigint plant_id FK "대상 식물"
        varchar image_url "S3 이미지 URL"
        timestamp created_at "촬영 시간 (매일 정오)"
    }

    %% [11] 육아 일기 (Diaries)
    DIARIES {
        bigint diary_id PK "일기 ID"
        bigint user_id FK "작성자"
        bigint plant_id FK "대상 식물"
        varchar image_url "첨부 사진 URL"
        text content "일기 내용"
        varchar weather "작성 당시 날씨"
        timestamp created_at "작성 시간"
    }

    %% [12] 업적 뱃지 (Badges)
    BADGES {
        bigint badge_id PK "뱃지 ID"
        bigint user_id FK "획득 유저"
        varchar badge_type "뱃지 코드 (TOUCH_100)"
        timestamp acquired_at "획득 시간"
    }