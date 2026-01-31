package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.dto.ActivityType;
import com.ssafy.farmily.domain.plant.entity.PlantActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PlantActivityLogRepository extends JpaRepository<PlantActivityLog, Long> {
    
    /**
     * 특정 식물의 오늘 날짜 특정 활동 횟수 조회
     * @param plantId 식물 ID
     * @param type 활동 타입
     * @param date 조회 날짜
     * @return 오늘 수행한 해당 활동 횟수
     */
    @Query("SELECT COUNT(l) FROM PlantActivityLog l " +
           "WHERE l.plantId = :plantId " +
           "AND l.type = :type " +
           "AND FUNCTION('DATE', l.createdAt) = :date")
    long countByPlantIdAndTypeAndDate(
        @Param("plantId") Long plantId,
        @Param("type") ActivityType type,
        @Param("date") LocalDate date
    );
}
