package com.ssafy.farmily.domain.achievement.controller;

import com.ssafy.farmily.domain.achievement.entity.RefAchievement;
import com.ssafy.farmily.domain.achievement.service.RefAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class RefAchievementController {

    private final RefAchievementService refAchievementService;

    /**
     * 전체 업적 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<RefAchievement>> getAllAchievements() {
        List<RefAchievement> achievements = refAchievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }
}
