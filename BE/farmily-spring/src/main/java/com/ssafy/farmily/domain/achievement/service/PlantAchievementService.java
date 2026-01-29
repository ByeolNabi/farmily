package com.ssafy.farmily.domain.achievement.service;

import com.ssafy.farmily.domain.achievement.entity.PlantAchievement;
import com.ssafy.farmily.domain.achievement.entity.RefAchievement;
import com.ssafy.farmily.domain.achievement.repository.PlantAchievementRepository;
import com.ssafy.farmily.domain.achievement.repository.RefAchievementRepository;
import com.ssafy.farmily.domain.activity.repository.PlantActivityCountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantAchievementService {

    private final PlantAchievementRepository plantAchievementRepository;
    private final RefAchievementRepository refAchievementRepository;
    private final PlantActivityCountsRepository plantActivityCountsRepository;

    /**
     * 활동 업적 체크 및 자동 지급
     * TODO: RefAchievement에 code 필드가 없어서 현재는 기능 비활성화
     */
    @Transactional
    public void checkAndUnlockAchievements(Long userId, Long plantId) {
        // TODO: 업적 로직 구현 필요 (RefAchievement에 code 필드 추가 또는 ID기반 조회)
        log.debug("Achievement check skipped - code field removed from RefAchievement");
    }

    /**
     * 사용자의 획득 업적 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PlantAchievement> getUnlockedAchievements(Long plantId) {
        return plantAchievementRepository.findByPlantId(plantId);
    }
}
