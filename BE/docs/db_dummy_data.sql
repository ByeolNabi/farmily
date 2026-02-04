-- ============================================
-- Farmily 더미 데이터 (Dummy Data) - 대량 확장 버전
-- 실행 전 db_init.sql이 먼저 실행되어야 합니다.
-- ============================================

-- 1. Users (10명)
INSERT INTO users (id, email, password, name, profile_image_url, fcm_token) VALUES
(1, 'farmer1@farmily.com', 'hashed_password_123', '김농부', 'https://example.com/profiles/user1.jpg', 'fcm_token_1'),
(2, 'gardener2@farmily.com', 'hashed_password_456', '이정원', 'https://example.com/profiles/user2.jpg', 'fcm_token_2'),
(3, 'plant_lover@gmail.com', 'pwd_789', '박초록', 'https://example.com/profiles/user3.jpg', 'fcm_token_3'),
(4, 'botanist_choi@naver.com', 'pwd_abc', '최연구', 'https://example.com/profiles/user4.jpg', 'fcm_token_4'),
(5, 'daily_leaf@daum.net', 'pwd_def', '정일상', 'https://example.com/profiles/user5.jpg', 'fcm_token_5'),
(6, 'green_thumb@farmily.com', 'pwd_ghi', '윤가든', 'https://example.com/profiles/user6.jpg', 'fcm_token_6'),
(7, 'cactus_king@gmail.com', 'pwd_jkl', '한가시', 'https://example.com/profiles/user7.jpg', 'fcm_token_7'),
(8, 'flower_pwr@naver.com', 'pwd_mno', '오꽃님', 'https://example.com/profiles/user8.jpg', 'fcm_token_8'),
(9, 'urban_jungle@gmail.com', 'pwd_pqr', '임숲', 'https://example.com/profiles/user9.jpg', 'fcm_token_9'),
(10, 'sprout_master@farmily.com', 'pwd_stu', '조새싹', 'https://example.com/profiles/user10.jpg', 'fcm_token_10');

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 2. ref_plant_species (15종)
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
(10, '알로카시아', 'https://example.com/plants/alocasia.jpg', 24, '[20, 30]', 70, '[60, 90]', 55, '[45, 75]', 15000),
(11, '올리브 나무', 'https://example.com/plants/olive.jpg', 20, '[10, 30]', 40, '[30, 60]', 40, '[30, 50]', 30000),
(12, '로즈마리', 'https://example.com/plants/rosemary.jpg', 18, '[10, 25]', 50, '[40, 60]', 40, '[30, 50]', 25000),
(13, '테이블야자', 'https://example.com/plants/parlor_palm.jpg', 21, '[18, 26]', 60, '[50, 70]', 50, '[40, 60]', 5000),
(14, '금전수', 'https://example.com/plants/zz_plant.jpg', 22, '[16, 30]', 40, '[30, 60]', 30, '[20, 40]', 3000),
(15, '칼라테아', 'https://example.com/plants/calathea.jpg', 21, '[18, 25]', 70, '[60, 85]', 60, '[50, 70]', 4000);

SELECT setval('ref_plant_species_id_seq', (SELECT MAX(id) FROM ref_plant_species));

-- 3. ref_achievement (15개)
INSERT INTO ref_achievement (id, name, description, icon_url, action_type, required_count) VALUES
(1, '첫 쓰다듬기', '처음으로 식물을 쓰다듬었어요!', 'https://example.com/icons/touch_1.png', 'TOUCH', 1),
(2, '다정한 손길', '식물을 10번 쓰다듬었어요', 'https://example.com/icons/touch_10.png', 'TOUCH', 10),
(3, '사랑의 교감', '식물을 50번 쓰다듬었어요', 'https://example.com/icons/touch_50.png', 'TOUCH', 50),
(4, '첫 물주기', '처음으로 물을 주었어요!', 'https://example.com/icons/water_1.png', 'WATER', 1),
(5, '성실한 집사', '물을 10번 주었어요', 'https://example.com/icons/water_10.png', 'WATER', 10),
(6, '오아시스', '물을 30번 주었어요', 'https://example.com/icons/water_30.png', 'WATER', 30),
(7, '첫 칭찬', '처음으로 식물을 칭찬했어요!', 'https://example.com/icons/praise_1.png', 'PRAISE', 1),
(8, '칭찬 박사', '칭찬을 20번 했어요', 'https://example.com/icons/praise_20.png', 'PRAISE', 20),
(9, '첫 대화', '처음으로 식물과 대화했어요!', 'https://example.com/icons/talk_1.png', 'TALK', 1),
(10, '수다쟁이', '대화를 15번 나누었어요', 'https://example.com/icons/talk_15.png', 'TALK', 15),
(11, '첫 일기', '처음으로 식물 일기를 작성했어요!', 'https://example.com/icons/diary_1.png', 'DIARY', 1),
(12, '기록의 왕', '일기를 10번 썼어요', 'https://example.com/icons/diary_10.png', 'DIARY', 10),
(13, '함께한 한 달', '식물과 함께한 지 30일!', 'https://example.com/icons/period_30.png', 'SYSTEM', 30),
(14, '건강 지킴이', '질병 진단을 5번 받았어요', 'https://example.com/icons/check_5.png', 'SYSTEM', 5),
(15, '베스트 프렌드', '모든 활동 총합 100회 달성', 'https://example.com/icons/best_friend.png', 'SYSTEM', 100);

SELECT setval('ref_achievement_id_seq', (SELECT MAX(id) FROM ref_achievement));

-- 4. ref_plant_disease (10개)
INSERT INTO ref_plant_disease (id, name, symptoms, solution) VALUES
(1, '흰가루병', '잎에 흰 가루 같은 곰팡이가 생기며, 잎이 노랗게 변함', '감염된 잎 제거, 통풍 개선, 살균제 분무'),
(2, '잎마름병', '잎 끝부터 갈색으로 마르며 점점 퍼져나감', '과습 방지, 감염 부위 제거, 물빠짐 개선'),
(3, '점무늬병', '잎에 갈색 또는 검은색 점이 생김', '환기 자주 시키기, 잎에 물 닿지 않게 주의'),
(4, '응애', '잎 뒷면에 아주 작은 거미줄이 생기고 잎이 변색됨', '잎 뒷면 세척, 습도 높이기, 전용 약제 사용'),
(5, '진딧물', '줄기나 잎에 작은 벌레가 붙어 즙액을 빨아먹음', '천연 살충제 사용, 흐르는 물에 씻어내기'),
(6, '무름병', '줄기나 뿌리 조직이 흐물흐물해지고 악취가 남', '즉시 배수 개선, 감염 부위 제거 후 소독'),
(7, '깍지벌레', '솜뭉치 같은 흰색 벌레가 붙어 있음', '핀셋으로 제거, 알코올 묻힌 솜으로 닦기'),
(8, '과습 스트레스', '잎이 노랗게 변하고 축 늘어짐', '물주기 중단, 흙 말리기, 분갈이 고려'),
(9, '비료 과다', '잎 끝이 타는 듯이 갈색으로 변함', '흙을 물로 씻어내거나 분갈이'),
(10, '햇빛 부족', '줄기가 웃자라고 잎 사이 간격이 넓어짐', '서서히 밝은 곳으로 이동');

SELECT setval('ref_plant_disease_id_seq', (SELECT MAX(id) FROM ref_plant_disease));

-- 5. Plant (15개)
INSERT INTO plant (id, users_id, ref_plant_species_id, nickname, profile_image_url, health_status, health_checked_at, love_temperature, is_active, station_point, started_at) VALUES
(1, 1, 1, '몬몬이', 'https://example.com/plants/u1_p1.jpg', 'healthy', '2026-02-04 10:00:00+09', 85.50, TRUE, 'POINT(2.5 3.2)', '2025-06-15 00:00:00+09'),
(2, 1, 3, '고무고무', 'https://example.com/plants/u1_p2.jpg', 'healthy', '2026-02-04 10:00:00+09', 45.00, TRUE, 'POINT(1.0 4.5)', '2025-09-01 00:00:00+09'),
(3, 2, 2, '뚜기', 'https://example.com/plants/u2_p1.jpg', 'healthy', '2026-02-04 10:00:00+09', 60.25, TRUE, 'POINT(3.0 1.5)', '2025-08-10 00:00:00+09'),
(4, 3, 5, '야자수', 'https://example.com/plants/u3_p1.jpg', 'warning', '2026-02-03 15:00:00+09', 30.00, TRUE, 'POINT(5.0 5.0)', '2025-11-20 00:00:00+09'),
(5, 4, 11, '올리버', 'https://example.com/plants/u4_p1.jpg', 'healthy', '2026-02-04 09:00:00+09', 92.10, TRUE, 'POINT(0.0 0.0)', '2025-05-10 00:00:00+09'),
(6, 5, 6, '포포', 'https://example.com/plants/u5_p1.jpg', 'healthy', '2026-02-04 11:00:00+09', 55.40, TRUE, 'POINT(1.2 2.3)', '2026-01-01 00:00:00+09'),
(7, 6, 12, '마리', 'https://example.com/plants/u6_p1.jpg', 'healthy', '2026-02-04 10:30:00+09', 78.00, TRUE, 'POINT(4.5 0.5)', '2025-10-15 00:00:00+09'),
(8, 7, 7, '선인장', 'https://example.com/plants/u7_p1.jpg', 'healthy', '2026-02-04 08:00:00+09', 25.00, TRUE, 'POINT(2.2 1.1)', '2025-12-25 00:00:00+09'),
(9, 8, 15, '칼라', 'https://example.com/plants/u8_p1.jpg', 'healthy', '2026-02-03 18:00:00+09', 66.70, TRUE, 'POINT(3.3 4.4)', '2025-07-20 00:00:00+09'),
(10, 9, 9, '행운이', 'https://example.com/plants/u9_p1.jpg', 'healthy', '2026-02-04 10:00:00+09', 50.00, TRUE, 'POINT(1.5 1.5)', '2025-03-10 00:00:00+09'),
(11, 10, 14, '부자', 'https://example.com/plants/u10_p1.jpg', 'healthy', '2026-02-04 12:00:00+09', 88.88, TRUE, 'POINT(0.5 5.5)', '2025-01-01 00:00:00+09'),
(12, 1, 13, '테이블', 'https://example.com/plants/u1_p3.jpg', 'healthy', '2026-02-04 10:00:00+09', 40.00, TRUE, 'POINT(4.0 4.0)', '2026-01-15 00:00:00+09'),
(13, 2, 4, '산세', 'https://example.com/plants/u2_p2.jpg', 'healthy', '2026-02-04 10:00:00+09', 35.50, TRUE, 'POINT(2.0 2.0)', '2025-09-15 00:00:00+09'),
(14, 3, 10, '알록', 'https://example.com/plants/u3_p2.jpg', 'warning', '2026-02-01 10:00:00+09', 20.00, TRUE, 'POINT(1.0 1.0)', '2025-10-01 00:00:00+09'),
(15, 4, 8, '필로', 'https://example.com/plants/u4_p2.jpg', 'healthy', '2026-02-04 10:00:00+09', 70.00, TRUE, 'POINT(5.5 0.5)', '2025-08-20 00:00:00+09');

SELECT setval('plant_id_seq', (SELECT MAX(id) FROM plant));

-- 6. plant_diary (20개)
INSERT INTO plant_diary (id, plant_id, content, image_url, happened_at, updated_at) VALUES
(1, 1, '오늘 몬몬이에게 처음으로 새 잎이 났어요! 🌱', 'https://example.com/diary/d1.jpg', '2025-07-01 10:30:00+09', '2025-07-01 10:30:00+09'),
(2, 1, '햇볕이 잘 드는 곳으로 옮겨줬어요.', 'https://example.com/diary/d2.jpg', '2025-08-15 14:00:00+09', '2025-08-15 14:00:00+09'),
(3, 2, '고무나무 잎을 닦아주니 반짝거리네요.', 'https://example.com/diary/d3.jpg', '2025-09-10 11:00:00+09', '2025-09-10 11:00:00+09'),
(4, 3, '스투키는 물을 자주 안 줘도 되어서 편해요.', 'https://example.com/diary/d4.jpg', '2025-10-01 09:00:00+09', '2025-10-01 09:00:00+09'),
(5, 4, '야자수 잎이 조금 마른 것 같아 걱정이에요.', 'https://example.com/diary/d5.jpg', '2025-12-05 16:00:00+09', '2025-12-05 16:00:00+09'),
(6, 5, '올리브 나무가 드디어 적응한 것 같아요.', 'https://example.com/diary/d6.jpg', '2025-06-20 10:00:00+09', '2025-06-20 10:00:00+09'),
(7, 6, '포토스 번식시키기 성공!', 'https://example.com/diary/d7.jpg', '2026-01-20 13:00:00+09', '2026-01-20 13:00:00+09'),
(8, 7, '로즈마리 향기가 너무 좋아요.', 'https://example.com/diary/d8.jpg', '2025-11-11 11:11:00+09', '2025-11-11 11:11:00+09'),
(9, 8, '선인장이 꽃을 피울까요?', 'https://example.com/diary/d9.jpg', '2026-01-05 10:00:00+09', '2026-01-05 10:00:00+09'),
(10, 9, '칼라테아 잎 무늬가 예술입니다.', 'https://example.com/diary/d10.jpg', '2025-08-30 18:00:00+09', '2025-08-30 18:00:00+09'),
(11, 10, '행운목에서 좋은 기운이 나길!', 'https://example.com/diary/d11.jpg', '2025-04-12 10:00:00+09', '2025-04-12 10:00:00+09'),
(12, 11, '금전수가 들어오고 나서 일이 잘 풀려요.', 'https://example.com/diary/d12.jpg', '2025-02-15 15:00:00+09', '2025-02-15 15:00:00+09'),
(13, 12, '테이블야자 수경재배 시작했어요.', 'https://example.com/diary/d13.jpg', '2026-01-25 10:00:00+09', '2026-01-25 10:00:00+09'),
(14, 1, '분갈이를 해줬어요. 더 크게 자라렴!', 'https://example.com/diary/d14.jpg', '2026-01-10 11:00:00+09', '2026-01-10 11:00:00+09'),
(15, 2, '오늘은 영양제를 줬어요.', 'https://example.com/diary/d15.jpg', '2026-01-15 10:00:00+09', '2026-01-15 10:00:00+09'),
(16, 13, '산세베리아는 밤에 산소를 많이 내뱉는데요.', 'https://example.com/diary/d16.jpg', '2025-10-20 22:00:00+09', '2025-10-20 22:00:00+09'),
(17, 14, '알로카시아 잎이 너무 커졌어요.', 'https://example.com/diary/d17.jpg', '2025-11-30 10:00:00+09', '2025-11-30 10:00:00+09'),
(18, 15, '필로덴드론 줄기가 길게 뻗어가네요.', 'https://example.com/diary/d18.jpg', '2025-09-25 14:00:00+09', '2025-09-25 14:00:00+09'),
(19, 1, '사랑한다고 말해줬더니 기분 좋아 보여요.', 'https://example.com/diary/d19.jpg', '2026-02-01 10:00:00+09', '2026-02-01 10:00:00+09'),
(20, 5, '첫 수확을 기대해 봅니다.', 'https://example.com/diary/d20.jpg', '2025-07-15 09:00:00+09', '2025-07-15 09:00:00+09');

SELECT setval('plant_diary_id_seq', (SELECT MAX(id) FROM plant_diary));

-- 7. plant_activity_log (30개 이상)
INSERT INTO plant_activity_log (id, plant_id, type, created_at) VALUES
(1, 1, 'WATER', '2026-02-01 08:00:00+09'),
(2, 1, 'TOUCH', '2026-02-01 10:00:00+09'),
(3, 1, 'PRAISE', '2026-02-01 12:00:00+09'),
(4, 2, 'WATER', '2026-02-01 09:00:00+09'),
(5, 3, 'TOUCH', '2026-02-02 10:00:00+09'),
(6, 4, 'TALK', '2026-02-02 11:00:00+09'),
(7, 5, 'WATER', '2026-02-02 08:00:00+09'),
(8, 1, 'DIARY', '2026-02-01 10:30:00+09'),
(9, 6, 'TOUCH', '2026-02-03 09:00:00+09'),
(10, 7, 'WATER', '2026-02-03 07:00:00+09'),
(11, 8, 'PRAISE', '2026-02-03 15:00:00+09'),
(12, 9, 'TALK', '2026-02-03 18:00:00+09'),
(13, 10, 'WATER', '2026-02-04 08:00:00+09'),
(14, 11, 'TOUCH', '2026-02-04 09:00:00+09'),
(15, 12, 'PRAISE', '2026-02-04 10:00:00+09'),
(16, 13, 'TALK', '2026-02-04 11:00:00+09'),
(17, 14, 'WATER', '2026-02-04 12:00:00+09'),
(18, 15, 'TOUCH', '2026-02-04 13:00:00+09'),
(19, 1, 'TOUCH', '2026-02-04 14:00:00+09'),
(20, 1, 'TOUCH', '2026-02-04 14:05:00+09'),
(21, 1, 'TOUCH', '2026-02-04 14:10:00+09'),
(22, 2, 'PRAISE', '2026-02-04 15:00:00+09'),
(23, 3, 'WATER', '2026-02-04 16:00:00+09'),
(24, 4, 'TOUCH', '2026-02-04 17:00:00+09'),
(25, 5, 'TALK', '2026-02-04 18:00:00+09'),
(26, 6, 'WATER', '2026-02-04 19:00:00+09'),
(27, 7, 'TOUCH', '2026-02-04 20:00:00+09'),
(28, 8, 'DIARY', '2026-01-05 10:00:00+09'),
(29, 9, 'PRAISE', '2026-02-04 21:00:00+09'),
(30, 10, 'TALK', '2026-02-04 22:00:00+09');

SELECT setval('plant_activity_log_id_seq', (SELECT MAX(id) FROM plant_activity_log));

-- 8. plant_activity_counts (15개 이상)
INSERT INTO plant_activity_counts (id, plant_id, activity_type, total_count, updated_at) VALUES
(1, 1, 'TOUCH', 15, '2026-02-04 14:10:00+09'),
(2, 1, 'WATER', 5, '2026-02-01 08:00:00+09'),
(3, 1, 'PRAISE', 10, '2026-02-01 12:00:00+09'),
(4, 2, 'WATER', 8, '2026-02-01 09:00:00+09'),
(5, 3, 'TOUCH', 12, '2026-02-02 10:00:00+09'),
(6, 4, 'TALK', 20, '2026-02-02 11:00:00+09'),
(7, 5, 'WATER', 100, '2026-02-02 08:00:00+09'),
(8, 6, 'TOUCH', 5, '2026-02-03 09:00:00+09'),
(9, 7, 'WATER', 7, '2026-02-03 07:00:00+09'),
(10, 8, 'PRAISE', 15, '2026-02-03 15:00:00+09'),
(11, 9, 'TALK', 3, '2026-02-03 18:00:00+09'),
(12, 10, 'WATER', 12, '2026-02-04 08:00:00+09'),
(13, 11, 'TOUCH', 50, '2026-02-04 09:00:00+09'),
(14, 12, 'PRAISE', 2, '2026-02-04 10:00:00+09'),
(15, 13, 'TALK', 1, '2026-02-04 11:00:00+09');

SELECT setval('plant_activity_counts_id_seq', (SELECT MAX(id) FROM plant_activity_counts));

-- 9. plant_achievement (15개 이상)
INSERT INTO plant_achievement (id, plant_id, ref_achievement_id, created_at) VALUES
(1, 1, 1, '2025-06-15 10:00:00+09'),
(2, 1, 4, '2025-06-16 08:00:00+09'),
(3, 1, 2, '2025-07-20 12:00:00+09'),
(4, 2, 4, '2025-09-02 09:00:00+09'),
(5, 3, 1, '2025-08-11 10:00:00+09'),
(6, 4, 9, '2025-11-21 11:00:00+09'),
(7, 5, 4, '2025-05-11 08:00:00+09'),
(8, 5, 5, '2025-06-10 08:00:00+09'),
(9, 5, 6, '2025-08-01 08:00:00+09'),
(10, 6, 1, '2026-01-02 09:00:00+09'),
(11, 7, 4, '2025-10-16 07:00:00+09'),
(12, 8, 7, '2025-12-26 15:00:00+09'),
(13, 11, 1, '2025-01-02 09:00:00+09'),
(14, 11, 2, '2025-01-15 09:00:00+09'),
(15, 11, 3, '2025-03-01 09:00:00+09');

SELECT setval('plant_achievement_id_seq', (SELECT MAX(id) FROM plant_achievement));

-- 10. plant_timelapse (20개 이상)
INSERT INTO plant_timelapse (id, plant_id, image_url, created_at) VALUES
(1, 1, 'https://example.com/tl/p1_1.jpg', '2025-06-15 12:00:00+09'),
(2, 1, 'https://example.com/tl/p1_2.jpg', '2025-07-15 12:00:00+09'),
(3, 1, 'https://example.com/tl/p1_3.jpg', '2025-08-15 12:00:00+09'),
(4, 1, 'https://example.com/tl/p1_4.jpg', '2025-09-15 12:00:00+09'),
(5, 1, 'https://example.com/tl/p1_5.jpg', '2025-10-15 12:00:00+09'),
(6, 2, 'https://example.com/tl/p2_1.jpg', '2025-09-01 12:00:00+09'),
(7, 2, 'https://example.com/tl/p2_2.jpg', '2025-10-01 12:00:00+09'),
(8, 3, 'https://example.com/tl/p3_1.jpg', '2025-08-10 12:00:00+09'),
(9, 3, 'https://example.com/tl/p3_2.jpg', '2025-09-10 12:00:00+09'),
(10, 4, 'https://example.com/tl/p4_1.jpg', '2025-11-20 12:00:00+09'),
(11, 4, 'https://example.com/tl/p4_2.jpg', '2026-02-03 12:00:00+09'),
(12, 5, 'https://example.com/tl/p5_1.jpg', '2025-05-10 12:00:00+09'),
(13, 6, 'https://example.com/tl/p6_1.jpg', '2026-01-01 12:00:00+09'),
(14, 7, 'https://example.com/tl/p7_1.jpg', '2025-10-15 12:00:00+09'),
(15, 8, 'https://example.com/tl/p8_1.jpg', '2025-12-25 12:00:00+09'),
(16, 9, 'https://example.com/tl/p9_1.jpg', '2025-07-20 12:00:00+09'),
(17, 10, 'https://example.com/tl/p10_1.jpg', '2025-03-10 12:00:00+09'),
(18, 11, 'https://example.com/tl/p11_1.jpg', '2025-01-01 12:00:00+09'),
(19, 12, 'https://example.com/tl/p12_1.jpg', '2026-01-15 12:00:00+09'),
(20, 13, 'https://example.com/tl/p13_1.jpg', '2025-09-15 12:00:00+09');

SELECT setval('plant_timelapse_id_seq', (SELECT MAX(id) FROM plant_timelapse));

-- 11. plant_health_logs (10개 이상)
INSERT INTO plant_health_logs (id, plant_timelapse_id, ref_plant_disease_id, bbox, confidence, created_at) VALUES
(1, 4, 2, '{"x1": 10, "y1": 20, "x2": 50, "y2": 60}', 45.50, '2025-09-15 12:00:00+09'),
(2, 11, 8, '{"x1": 30, "y1": 30, "x2": 80, "y2": 80}', 72.30, '2026-02-03 12:00:00+09'),
(3, 5, 1, '{"x1": 5, "y1": 5, "x2": 25, "y2": 25}', 88.00, '2025-10-15 12:00:00+09'),
(4, 1, 10, '{"x1": 0, "y1": 0, "x2": 100, "y2": 100}', 30.00, '2025-06-15 12:00:00+09'),
(5, 10, 3, '{"x1": 40, "y1": 40, "x2": 60, "y2": 60}', 65.00, '2025-11-20 12:00:00+09'),
(6, 15, 4, '{"x1": 20, "y1": 10, "x2": 30, "y2": 40}', 55.00, '2025-12-25 12:00:00+09'),
(7, 20, 6, '{"x1": 10, "y1": 80, "x2": 40, "y2": 95}', 91.00, '2025-09-15 12:00:00+09'),
(8, 2, 8, '{"x1": 0, "y1": 0, "x2": 100, "y2": 100}', 42.00, '2025-07-15 12:00:00+09'),
(9, 7, 9, '{"x1": 50, "y1": 50, "x2": 90, "y2": 90}', 77.00, '2025-10-01 12:00:00+09'),
(10, 12, 1, '{"x1": 10, "y1": 10, "x2": 30, "y2": 30}', 60.00, '2025-05-10 12:00:00+09');

SELECT setval('plant_health_logs_id_seq', (SELECT MAX(id) FROM plant_health_logs));

-- 12. plant_sensor_log (20개 이상)
INSERT INTO plant_sensor_log (id, plant_id, temperature, humidity, soil_moisture, illuminance, created_at) VALUES
(1, 1, 22.50, 58.20, 48.50, 9500.00, '2026-02-04 08:00:00+09'),
(2, 1, 23.00, 57.50, 48.00, 10500.00, '2026-02-04 09:00:00+09'),
(3, 1, 24.50, 55.00, 47.50, 12000.00, '2026-02-04 10:00:00+09'),
(4, 2, 21.00, 40.00, 30.00, 8000.00, '2026-02-04 08:00:00+09'),
(5, 2, 22.00, 39.50, 29.50, 9000.00, '2026-02-04 09:00:00+09'),
(6, 3, 20.50, 45.00, 35.00, 7500.00, '2026-02-04 08:00:00+09'),
(7, 4, 25.00, 65.00, 55.00, 15000.00, '2026-02-04 08:00:00+09'),
(8, 5, 23.50, 40.00, 40.00, 25000.00, '2026-02-04 08:00:00+09'),
(9, 6, 22.00, 55.00, 50.00, 8000.00, '2026-02-04 08:00:00+09'),
(10, 7, 21.50, 50.00, 40.00, 20000.00, '2026-02-04 08:00:00+09'),
(11, 8, 20.00, 50.00, 40.00, 12000.00, '2026-02-04 08:00:00+09'),
(12, 9, 24.00, 70.00, 60.00, 14000.00, '2026-02-04 08:00:00+09'),
(13, 10, 22.00, 60.00, 60.00, 8000.00, '2026-02-04 08:00:00+09'),
(14, 11, 23.00, 40.00, 30.00, 3000.00, '2026-02-04 08:00:00+09'),
(15, 12, 21.00, 60.00, 50.00, 5000.00, '2026-02-04 08:00:00+09'),
(16, 1, 25.00, 54.00, 47.00, 13000.00, '2026-02-04 11:00:00+09'),
(17, 1, 25.50, 53.00, 46.50, 14000.00, '2026-02-04 12:00:00+09'),
(18, 1, 26.00, 52.00, 46.00, 15000.00, '2026-02-04 13:00:00+09'),
(19, 1, 25.50, 51.00, 45.50, 14500.00, '2026-02-04 14:00:00+09'),
(20, 1, 25.00, 50.00, 45.00, 13500.00, '2026-02-04 15:00:00+09');

SELECT setval('plant_sensor_log_id_seq', (SELECT MAX(id) FROM plant_sensor_log));
