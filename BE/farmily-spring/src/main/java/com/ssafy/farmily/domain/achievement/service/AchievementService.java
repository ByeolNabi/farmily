package com.ssafy.farmily.domain.achievement.service;

import com.ssafy.farmily.domain.achievement.dto.AchievementResponse;
import com.ssafy.farmily.domain.achievement.entity.Achievement;
import com.ssafy.farmily.domain.achievement.entity.PlantAchievement;
import com.ssafy.farmily.domain.achievement.entity.PlantActivityCounts;
import com.ssafy.farmily.domain.achievement.repository.AchievementRepository;
import com.ssafy.farmily.domain.achievement.repository.PlantAchievementRepository;
import com.ssafy.farmily.domain.achievement.repository.PlantActivityCountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PlantAchievementRepository plantAchievementRepository;
    private final PlantActivityCountsRepository plantActivityCountsRepository;

    /**
     * 활동 기록 및 업적(뱃지) 획득 체크
     */
    @Transactional
    public void trackActivity(Long plantId, String activityType) {
        // 1. 활동 카운트 증가 또는 생성
        PlantActivityCounts counts = plantActivityCountsRepository.findByPlantIdAndActivityType(plantId, activityType)
                .orElseGet(() -> PlantActivityCounts.builder()
                        .plantId(plantId)
                        .activityType(activityType)
                        .totalCount(0)
                        .build());
        
        counts.incrementCount();
        plantActivityCountsRepository.save(counts);

        // 2. 획득 가능한 업적 체크
        checkAndGrantActivityAchievements(plantId, activityType, counts.getTotalCount());
    }

    private void checkAndGrantActivityAchievements(Long plantId, String activityType, int currentCount) {
        // 해당 활동과 횟수에 정확히 매칭되는 업적 조회 (10, 50, 100 등)
        achievementRepository.findByActionTypeAndRequiredCount(activityType, currentCount)
                .ifPresent(achievement -> grantAchievementIfNotExists(plantId, achievement));
    }

    /**
     * 기념일 업적 획득 체크
     */
    @Transactional
    public void checkAndGrantAnniversaryAchievement(Long plantId, long daysSinceStart) {
        // "ANNIVERSARY" 타입의 업적 중 requiredCount가 daysSinceStart와 일치하는 것 조회
        achievementRepository.findByActionTypeAndRequiredCount("ANNIVERSARY", (int) daysSinceStart)
                .ifPresent(achievement -> grantAchievementIfNotExists(plantId, achievement));
    }

    private void grantAchievementIfNotExists(Long plantId, Achievement achievement) {
        if (!plantAchievementRepository.existsByPlantIdAndAchievementId(plantId, achievement.getId())) {
            PlantAchievement plantAchievement = PlantAchievement.builder()
                    .plantId(plantId)
                    .achievement(achievement)
                    .build();
            plantAchievementRepository.save(plantAchievement);
            log.info("업적 달성! 식물ID: {}, 업적: {}", plantId, achievement.getName());
        }
    }

    public List<AchievementResponse> getAchievements(Long plantId) {
        return plantAchievementRepository.findByPlantIdOrderByCreatedAtDesc(plantId).stream()
                .map(AchievementResponse::from)
                .collect(Collectors.toList());
    }
}
