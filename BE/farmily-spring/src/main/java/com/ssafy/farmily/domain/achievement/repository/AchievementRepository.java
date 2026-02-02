package com.ssafy.farmily.domain.achievement.repository;

import com.ssafy.farmily.domain.achievement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByActionType(String actionType);
    Optional<Achievement> findByActionTypeAndRequiredCount(String actionType, Integer requiredCount);
}
