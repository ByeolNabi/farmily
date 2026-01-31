package com.ssafy.farmily.domain.plant.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * 포인트 지급 정책
 * 각 활동별 기본 점수와 1일 최대 인정 횟수를 관리
 */
@Getter
@RequiredArgsConstructor
public enum PointAction {
    TOUCH(ActivityType.TOUCH, new BigDecimal("0.01"), 3),      // 쓰다듬기: 0.01점, 1일 3회
    WATER(ActivityType.WATER, new BigDecimal("0.05"), 1),      // 물주기: 0.05점, 1일 1회
    TALK(ActivityType.TALK, new BigDecimal("0.02"), 1),        // 대화: 0.02점, 1일 1회
    PRAISE(ActivityType.PRAISE, new BigDecimal("0.02"), 1),    // 칭찬: 0.02점, 1일 1회
    DIARY(ActivityType.DIARY, new BigDecimal("0.08"), 1);      // 일기쓰기: 0.08점, 1일 1회

    private final ActivityType activityType;  // DB 저장용 타입
    private final BigDecimal basePoint;       // 기본 점수
    private final int dailyLimit;             // 1일 최대 횟수
}
