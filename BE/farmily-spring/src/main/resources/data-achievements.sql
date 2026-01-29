-- 업적 초기 데이터 삽입
-- 활동 업적 (쓰다듬기)
INSERT INTO achievements (code, name, description, action_type, action_count) VALUES
('PETTING_10', '첫 교감', '식물을 10번 쓰다듬었어요', 'ACTIVITY', 10),
('PETTING_50', '스킨십 달인', '식물을 50번 쓰다듬었어요', 'ACTIVITY', 50),
('PETTING_100', '터치 마스터', '식물을 100번 쓰다듬었어요', 'ACTIVITY', 100);

-- 활동 업적 (물주기)
INSERT INTO achievements (code, name, description, action_type, action_count) VALUES
('WATERING_10', '물주기 입문', '식물에게 10번 물을 줬어요', 'ACTIVITY', 10),
('WATERING_50', '물주기 달인', '식물에게 50번 물을 줬어요', 'ACTIVITY', 50),
('WATERING_100', '관수 마스터', '식물에게 100번 물을 줬어요', 'ACTIVITY', 100);

-- 활동 업적 (대화)
INSERT INTO achievements (code, name, description, action_type, action_count) VALUES
('CONVERSATION_10', '수다쟁이', '식물과 10번 대화했어요', 'ACTIVITY', 10),
('CONVERSATION_50', '말벗이 되다', '식물과 50번 대화했어요', 'ACTIVITY', 50),
('CONVERSATION_100', '대화 마스터', '식물과 100번 대화했어요', 'ACTIVITY', 100);

-- 기념일 업적
INSERT INTO achievements (code, name, description, action_type, action_count) VALUES
('DAY_1', '운명적 만남', '식물과 함께한 첫날', 'ANNIVERSARY', 1),
('DAY_100', '백일의 기적', '식물과 함께한 지 100일!', 'ANNIVERSARY', 100),
('DAY_365', '1주년 축하', '식물과 함께한 지 1년!', 'ANNIVERSARY', 365);
