package com.ssafy.farmily.domain.plant.dto;

/**
 * 식물 활동 타입
 * DB에 저장되는 enum 값
 */
public enum ActivityType {
    TOUCH,    // 쓰다듬기
    WATER,    // 물주기
    TALK,     // 대화
    PRAISE,   // 칭찬
    DIARY     // 일기쓰기
}
