package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    
    /**
     * 사용자 ID로 식물 조회 (여러 개 있을 수 있음)
     */
    List<Plant> findByUserId(Long userId);

    /**
     * 활성화된 모든 식물 조회 (스케줄러용)
     */
    java.util.List<Plant> findByIsActiveTrue();
}
