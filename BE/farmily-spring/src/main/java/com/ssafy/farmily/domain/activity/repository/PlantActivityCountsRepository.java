package com.ssafy.farmily.domain.activity.repository;

import com.ssafy.farmily.domain.activity.entity.PlantActivityCounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantActivityCountsRepository extends JpaRepository<PlantActivityCounts, Long> {
    
    Optional<PlantActivityCounts> findByPlantIdAndActivityType(Long plantId, String activityType);
    
    Long countByPlantIdAndActivityType(Long plantId, String activityType);
}
