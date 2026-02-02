package com.ssafy.farmily.domain.achievement.repository;

import com.ssafy.farmily.domain.achievement.entity.PlantAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantAchievementRepository extends JpaRepository<PlantAchievement, Long> {
    List<PlantAchievement> findByPlantIdOrderByCreatedAtDesc(Long plantId);
    boolean existsByPlantIdAndAchievementId(Long plantId, Long achievementId);
}
