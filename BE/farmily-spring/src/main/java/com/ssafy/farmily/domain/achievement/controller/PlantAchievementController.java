package com.ssafy.farmily.domain.achievement.controller;

import com.ssafy.farmily.domain.achievement.entity.PlantAchievement;
import com.ssafy.farmily.domain.achievement.service.PlantAchievementService;
import com.ssafy.farmily.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class PlantAchievementController {

    private final PlantAchievementService plantAchievementService;
    private final AuthUtil authUtil;

    /**
     * 획득한 업적 목록 조회
     */
    @GetMapping("/unlocked")
    public ResponseEntity<List<PlantAchievement>> getUnlockedAchievements(
            @RequestParam Long plantId) {
        List<PlantAchievement> achievements = plantAchievementService.getUnlockedAchievements(plantId);
        return ResponseEntity.ok(achievements);
    }
}
