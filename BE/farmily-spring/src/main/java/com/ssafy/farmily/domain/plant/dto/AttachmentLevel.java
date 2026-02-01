package com.ssafy.farmily.domain.plant.dto;

import java.math.BigDecimal;

/**
 * 애착 등급 Enum
 * 0-100 범위의 애착점수를 5단계로 분류
 */
public enum AttachmentLevel {
    AWKWARD("어색한 사이", 0, 29),
    GETTING_FAMILIAR("익숙해지는 사이", 30, 49),
    BONDING("정이 드는 사이", 50, 69),
    OPENING_HEART("마음을 여는 사이", 70, 89),
    COMPANION("반려 사이", 90, 100);

    private final String description;
    private final int minPoints;
    private final int maxPoints;

    AttachmentLevel(String description, int minPoints, int maxPoints) {
        this.description = description;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    public String getDescription() {
        return description;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * 애착점수로 등급 찾기
     * @param points 애착점수 (0-100)
     * @return 해당하는 애착 등급
     */
    public static AttachmentLevel fromPoints(BigDecimal points) {
        int pointValue = points.intValue();
        
        for (AttachmentLevel level : values()) {
            if (pointValue >= level.minPoints && pointValue <= level.maxPoints) {
                return level;
            }
        }
        
        // 기본값 (0점 미만 또는 100점 초과 시)
        return pointValue < 0 ? AWKWARD : COMPANION;
    }
}
