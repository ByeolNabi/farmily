package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.entity.PlantSensorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 식물 센서 로그 Repository
 */
@Repository
public interface PlantSensorLogRepository extends JpaRepository<PlantSensorLog, Long> {
}
