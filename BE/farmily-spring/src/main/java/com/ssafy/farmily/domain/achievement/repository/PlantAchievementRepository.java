package com.ssafy.farmily.domain.achievement.repository;

import com.ssafy.farmily.domain.achievement.entity.PlantAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantAchievementRepository extends JpaRepository<PlantAchievement, Long> {
    List<PlantAchievement> findByPlantId(Long plantId);
}
