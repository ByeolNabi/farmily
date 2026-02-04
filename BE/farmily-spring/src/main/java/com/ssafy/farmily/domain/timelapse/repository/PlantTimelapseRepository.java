package com.ssafy.farmily.domain.timelapse.repository;

import com.ssafy.farmily.domain.timelapse.entity.PlantTimelapse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 타임랩스 리포지토리
 */
@Repository
public interface PlantTimelapseRepository extends JpaRepository<PlantTimelapse, Long> {

    /**
     * 특정 식물의 타임랩스 목록을 촬영일 오름차순으로 조회 (과거 → 현재)
     */
    List<PlantTimelapse> findByPlantIdOrderByCreatedAtAsc(Long plantId);
}
