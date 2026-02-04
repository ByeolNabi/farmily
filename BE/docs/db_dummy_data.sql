-- ============================================
-- Farmily 더미 데이터 (Dummy Data) - 개선 버전
-- 실행 전 db_init.sql이 먼저 실행되어야 합니다.
-- ============================================

-- 1. Users
INSERT INTO users (id, email, password, name, profile_image_url, fcm_token) VALUES
(1, 'farmer1@farmily.com', 'hashed_password_123', '김농부', 'https://example.com/profiles/farmer1.jpg', 'fcm_token_user1_abc123'),
(2, 'gardener2@farmily.com', 'hashed_password_456', '이정원사', 'https://example.com/profiles/gardener2.jpg', 'fcm_token_user2_def456');

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 2. ref_plant_species (범위 데이터 문자열로 저장)
INSERT INTO ref_plant_species (id, name, image_url, temp_target, temp_range, humid_target, humid_range, soil_target, soil_range, illuminance) VALUES
(1, '몬스테라', 'https://example.com/plants/monstera.jpg', 22, '[18, 28]', 60, '[50, 80]', 50, '[40, 70]', 10000),
(2, '스투키', 'https://example.com/plants/stuckyi.jpg', 20, '[15, 30]', 40, '[30, 60]', 30, '[20, 50]', 8000),
(3, '고무나무', 'https://example.com/plants/rubber_tree.jpg', 23, '[18, 29]', 55, '[45, 75]', 45, '[35, 65]', 15000),
(4, '산세베리아', 'https://example.com/plants/sansevieria.jpg', 21, '[16, 30]', 45, '[35, 65]', 35, '[25, 55]', 12000),
(5, '아레카야자', 'https://example.com/plants/areca_palm.jpg', 24, '[20, 30]', 65, '[55, 85]', 55, '[45, 75]', 18000),
(6, '포토스', 'https://example.com/plants/pothos.jpg', 22, '[17, 28]', 55, '[45, 75]', 50, '[40, 70]', 8000),
(7, '피쉬본 선인장', 'https://example.com/plants/fishbone_cactus.jpg', 20, '[15, 27]', 50, '[40, 70]', 40, '[30, 60]', 12000),
(8, '필로덴드론', 'https://example.com/plants/philodendron.jpg', 23, '[18, 28]', 60, '[50, 80]', 55, '[45, 75]', 10000),
(9, '행운목', 'https://example.com/plants/lucky_bamboo.jpg', 22, '[18, 28]', 60, '[50, 80]', 60, '[50, 80]', 8000),
(10, '알로카시아', 'https://example.com/plants/alocasia.jpg', 24, '[20, 30]', 70, '[60, 90]', 55, '[45, 75]', 15000);

SELECT setval('ref_plant_species_id_seq', (SELECT MAX(id) FROM ref_plant_species));

-- 3. ref_achievement (생략 - 기존 유지)
INSERT INTO ref_achievement (id, name, description, icon_url, action_type, required_count) VALUES
(1, '첫 쓰다듬기', '처음으로 식물을 쓰다듬었어요!', 'https://example.com/icons/petting_1.png', 'petting', 1),
(2, '다정한 손길', '식물을 10번 쓰다듬었어요', 'https://example.com/icons/petting_10.png', 'petting', 10),
(5, '첫 물주기', '처음으로 물을 주었어요!', 'https://example.com/icons/watering_1.png', 'watering', 1),
(9, '첫 칭찬', '처음으로 식물을 칭찬했어요!', 'https://example.com/icons/praising_1.png', 'praising', 1),
(12, '첫 대화', '처음으로 식물과 대화했어요!', 'https://example.com/icons/talking_1.png', 'talking', 1),
(15, '첫 일기', '처음으로 식물 일기를 작성했어요!', 'https://example.com/icons/diary_1.png', 'diary', 1);

SELECT setval('ref_achievement_id_seq', (SELECT MAX(id) FROM ref_achievement));

-- 4. ref_plant_disease
INSERT INTO ref_plant_disease (id, name, symptoms, solution) VALUES
(1, '흰가루병', '잎에 흰 가루 같은 곰팡이가 생기며, 잎이 노랗게 변함', '감염된 잎 제거, 통풍 개선, 살균제 분무'),
(2, '잎마름병', '잎 끝부터 갈색으로 마르며 점점 퍼져나감', '과습 방지, 감염 부위 제거, 물빠짐 개선'),
(8, '과습 스트레스', '잎이 노랗게 변하고 축 늘어짐', '물주기 간격 늘리기, 배수 잘 되는 화분 사용');

SELECT setval('ref_plant_disease_id_seq', (SELECT MAX(id) FROM ref_plant_disease));

-- 5. Plant (station_point: WKT 문자열, love_temperature: 소수점 지원)
INSERT INTO plant (id, users_id, ref_plant_species_id, nickname, profile_image_url, health_status, health_checked_at, love_temperature, is_active, station_point, started_at) VALUES
(1, 1, 1, '몬몬이', 'https://example.com/my_plants/monmon.jpg', 'healthy', '2026-01-28 10:00:00+09', 75.50, TRUE, 'POINT(2.5 3.2)', '2025-06-15 00:00:00+09'),
(2, 1, 3, '고무고무', 'https://example.com/my_plants/gomu.jpg', 'healthy', '2026-01-27 15:30:00+09', 45.00, TRUE, 'POINT(1.0 4.5)', '2025-09-01 00:00:00+09'),
(4, 2, 2, '뚜기', 'https://example.com/my_plants/stucky.jpg', 'healthy', '2026-01-28 14:00:00+09', 60.25, TRUE, 'POINT(3.0 1.5)', '2025-08-10 00:00:00+09');

SELECT setval('plant_id_seq', (SELECT MAX(id) FROM plant));

-- 6. plant_diary
INSERT INTO plant_diary (id, plant_id, content, image_url, happened_at, updated_at) VALUES
(1, 1, '오늘 몬몬이에게 처음으로 새 잎이 났어요! 🌱', 'https://example.com/diary/1_new_leaf.jpg', '2025-07-01 10:30:00+09', '2025-07-01 10:30:00+09');

SELECT setval('plant_diary_id_seq', (SELECT MAX(id) FROM plant_diary));

-- 10. plant_timelapse
INSERT INTO plant_timelapse (id, plant_id, image_url, created_at) VALUES
(1, 1, 'https://example.com/timelapse/1_day1.jpg', '2025-06-15 12:00:00+09'),
(6, 1, 'https://example.com/timelapse/1_month3.jpg', '2025-09-15 12:00:00+09'),
(16, 4, 'https://example.com/timelapse/4_latest.jpg', '2026-01-28 12:00:00+09');

SELECT setval('plant_timelapse_id_seq', (SELECT MAX(id) FROM plant_timelapse));

-- 11. plant_health_logs (bbox 컬럼 추가하여 오류 해결)
INSERT INTO plant_health_logs (id, plant_timelapse_id, ref_plant_disease_id, bbox, confidence, created_at) VALUES
(1, 6, 2, '{"x1": 10, "y1": 20, "x2": 50, "y2": 60}', 45.50, '2025-09-15 12:00:00+09'),
(2, 16, 8, '{"x1": 30, "y1": 30, "x2": 80, "y2": 80}', 72.30, '2026-01-26 12:00:00+09');

SELECT setval('plant_health_logs_id_seq', (SELECT MAX(id) FROM plant_health_logs));

-- 12. plant_sensor_log
INSERT INTO plant_sensor_log (id, plant_id, temperature, humidity, soil_moisture, illuminance, created_at) VALUES
(1, 1, 22.50, 58.20, 48.50, 9500.00, '2026-01-22 08:00:00+09');

SELECT setval('plant_sensor_log_id_seq', (SELECT MAX(id) FROM plant_sensor_log));