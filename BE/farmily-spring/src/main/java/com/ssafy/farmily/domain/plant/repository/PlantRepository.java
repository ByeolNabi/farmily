package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    
    /**
     * 사용자 ID로 식물 조회 (여러 개 있을 수 있음)
     */
    Optional<Plant> findByUserId(Long userId);
}
