package com.ssafy.farmily.domain.healthlog.repository;

import com.ssafy.farmily.domain.healthlog.entity.RefPlantDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 식물 질병 참조 리포지토리
 */
@Repository
public interface RefPlantDiseaseRepository extends JpaRepository<RefPlantDisease, Long> {

    /**
     * 질병명으로 조회
     */
    Optional<RefPlantDisease> findByName(String name);
}
