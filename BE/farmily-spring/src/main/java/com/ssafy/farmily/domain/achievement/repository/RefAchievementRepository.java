package com.ssafy.farmily.domain.achievement.repository;

import com.ssafy.farmily.domain.achievement.entity.RefAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefAchievementRepository extends JpaRepository<RefAchievement, Long> {
}
