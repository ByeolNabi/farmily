package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.entity.PlantDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PlantDiary 리포지토리
 */
@Repository
public interface PlantDiaryRepository extends JpaRepository<PlantDiary, Long> {

    /**
     * 특정 식물의 모든 일기 조회 (최신순)
     */
    List<PlantDiary> findByPlantIdOrderByHappenedAtDesc(Long plantId);

    /**
     * 특정 식물의 일기 조회 (생성일 기준 최신순)
     */
    List<PlantDiary> findByPlantIdOrderByCreatedAtDesc(Long plantId);

    /**
     * 특정 식물의 특정 기간 일기 조회
     */
    List<PlantDiary> findByPlantIdAndHappenedAtBetweenOrderByHappenedAtDesc(
            Long plantId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    /**
     * 특정 식물의 일기 개수
     */
    long countByPlantId(Long plantId);

    /**
     * 특정 식물의 특정 일기 조회 (소유권 확인용)
     */
    boolean existsByIdAndPlantId(Long diaryId, Long plantId);
}
