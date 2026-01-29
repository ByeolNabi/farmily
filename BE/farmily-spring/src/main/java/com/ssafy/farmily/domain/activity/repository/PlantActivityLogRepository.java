package com.ssafy.farmily.domain.activity.repository;

import com.ssafy.farmily.domain.activity.entity.PlantActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantActivityLogRepository extends JpaRepository<PlantActivityLog, Long> {
    
    @Query("SELECT COUNT(p) FROM PlantActivityLog p WHERE p.plantId = :plantId AND p.type = :type")
    Long countByPlantIdAndType(Long plantId, String type);
}
