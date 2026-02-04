package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.entity.WaterAlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 물주기 알림 로그 Repository
 */
@Repository
public interface WaterAlertLogRepository extends JpaRepository<WaterAlertLog, Long> {

    /**
     * 특정 식물의 특정 타입 알림 중 가장 최근 기록 조회
     */
    @Query("SELECT w FROM WaterAlertLog w WHERE w.plantId = :plantId AND w.alertType = :alertType ORDER BY w.lastAlertAt DESC LIMIT 1")
    Optional<WaterAlertLog> findLatestByPlantIdAndAlertType(@Param("plantId") Long plantId, @Param("alertType") String alertType);

    /**
     * 특정 식물의 특정 타입 알림이 특정 시간 이후에 발송되었는지 확인
     */
    @Query("SELECT COUNT(w) > 0 FROM WaterAlertLog w WHERE w.plantId = :plantId AND w.alertType = :alertType AND w.lastAlertAt >= :since")
    boolean existsByPlantIdAndAlertTypeAndLastAlertAtAfter(@Param("plantId") Long plantId, @Param("alertType") String alertType, @Param("since") LocalDateTime since);
}
