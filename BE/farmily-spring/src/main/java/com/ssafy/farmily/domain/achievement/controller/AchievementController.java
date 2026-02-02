package com.ssafy.farmily.domain.achievement.controller;

import com.ssafy.farmily.domain.achievement.dto.AchievementResponse;
import com.ssafy.farmily.domain.achievement.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plants/{plantId}/achievements")
@RequiredArgsConstructor
@Tag(name = "Achievements", description = "뱃지/업적 관련 API")
public class AchievementController {

    private final AchievementService achievementService;

    @Operation(summary = "획득한 업적 목록 조회", description = "앱 전용 (JWT 필수)")
    @GetMapping
    public List<AchievementResponse> getPlantAchievements(@PathVariable Long plantId) {
        return achievementService.getAchievements(plantId);
    }
}
