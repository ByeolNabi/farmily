package com.ssafy.farmily.domain.plant.dto;

import java.math.BigDecimal;

/**
 * 애착 등급 조회 응답 DTO
 */
public record AttachmentLevelResponse(
    String level,              // "COMPANION"
    String description,        // "반려 사이"
    BigDecimal currentPoints,  // 95.67
    int minPoints,             // 90
    int maxPoints              // 100
) {
    public static AttachmentLevelResponse from(AttachmentLevel level, BigDecimal currentPoints) {
        return new AttachmentLevelResponse(
                level.name(),
                level.getDescription(),
                currentPoints,
                level.getMinPoints(),
                level.getMaxPoints()
        );
    }
}
