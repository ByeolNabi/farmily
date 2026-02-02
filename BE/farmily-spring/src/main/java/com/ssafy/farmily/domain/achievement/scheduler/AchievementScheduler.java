package com.ssafy.farmily.domain.achievement.scheduler;

import com.ssafy.farmily.domain.achievement.service.AchievementService;
import com.ssafy.farmily.domain.plant.entity.Plant;
import com.ssafy.farmily.domain.plant.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementScheduler {

    private final PlantRepository plantRepository;
    private final AchievementService achievementService;

    /**
     * 매일 자정 기념일 업적 체크
     * startedAt 기준으로 1일, 100일, 365일 경과했는지 확인
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkAnniversaryAchievements() {
        log.info("기념일 업적 체크 스케줄러 시작");
        
        List<Plant> plants = plantRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Plant plant : plants) {
            if (plant.getStartedAt() == null) continue;

            LocalDate startDate = plant.getStartedAt().toLocalDate();
            // D-Day 계산 (등록일이 1일차)
            long daysSince = ChronoUnit.DAYS.between(startDate, today) + 1;
            
            // 업적 부여 로직 호출 (1, 100, 365 등)
            achievementService.checkAndGrantAnniversaryAchievement(plant.getId(), daysSince);
        }
        
        log.info("기념일 업적 체크 스케줄러 종료");
    }
}
