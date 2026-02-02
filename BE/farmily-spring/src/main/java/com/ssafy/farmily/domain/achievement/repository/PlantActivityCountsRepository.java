package com.ssafy.farmily.domain.achievement.repository;

import com.ssafy.farmily.domain.achievement.entity.PlantActivityCounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantActivityCountsRepository extends JpaRepository<PlantActivityCounts, Long> {
    Optional<PlantActivityCounts> findByPlantIdAndActivityType(Long plantId, String activityType);
}
