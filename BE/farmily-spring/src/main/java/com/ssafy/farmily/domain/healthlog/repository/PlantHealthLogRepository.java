package com.ssafy.farmily.domain.healthlog.repository;

import com.ssafy.farmily.domain.healthlog.entity.PlantHealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 식물 건강 로그 리포지토리
 */
@Repository
public interface PlantHealthLogRepository extends JpaRepository<PlantHealthLog, Long> {

    /**
     * 타임랩스 ID로 건강 로그 조회
     */
    List<PlantHealthLog> findByPlantTimelapseIdOrderByCreatedAtDesc(Long plantTimelapseId);

    /**
     * 특정 식물의 모든 건강 로그 조회 (타임랩스 → 플랜트 조인 필요)
     */
    @Query("SELECT h FROM PlantHealthLog h " +
            "JOIN h.plantTimelapse t " +
            "WHERE t.plantId = :plantId " +
            "ORDER BY h.createdAt DESC")
    List<PlantHealthLog> findByPlantId(@Param("plantId") Long plantId);

    /**
     * 특정 식물의 최신 건강 로그 1건 조회
     */
    @Query("SELECT h FROM PlantHealthLog h " +
            "JOIN h.plantTimelapse t " +
            "WHERE t.plantId = :plantId " +
            "ORDER BY h.createdAt DESC " +
            "LIMIT 1")
    Optional<PlantHealthLog> findLatestByPlantId(@Param("plantId") Long plantId);
}
