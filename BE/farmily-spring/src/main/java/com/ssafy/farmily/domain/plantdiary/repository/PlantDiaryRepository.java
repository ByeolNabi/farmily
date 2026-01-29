package com.ssafy.farmily.domain.plantdiary.repository;

import com.ssafy.farmily.domain.plantdiary.entity.PlantDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantDiaryRepository extends JpaRepository<PlantDiary, Long> {
    List<PlantDiary> findByUserIdAndPlantIdOrderByRecordedAtDesc(Long userId, Long plantId);
    Optional<PlantDiary> findByIdAndUserId(Long id, Long userId);
}
