package com.ssafy.farmily.domain.achievement.dto;

import com.ssafy.farmily.domain.achievement.entity.Achievement;
import com.ssafy.farmily.domain.achievement.entity.PlantAchievement;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AchievementResponse(
        Long id,
        String name,
        String description,
        String iconUrl,
        String actionType,
        Integer requiredCount,
        LocalDateTime createdAt
) {
    public static AchievementResponse from(PlantAchievement plantAchievement) {
        Achievement achievement = plantAchievement.getAchievement();
        return AchievementResponse.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .iconUrl(achievement.getIconUrl())
                .actionType(achievement.getActionType())
                .requiredCount(achievement.getRequiredCount())
                .createdAt(plantAchievement.getCreatedAt())
                .build();
    }
}
