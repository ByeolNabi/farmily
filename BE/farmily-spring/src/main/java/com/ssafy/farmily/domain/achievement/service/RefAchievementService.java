package com.ssafy.farmily.domain.achievement.service;

import com.ssafy.farmily.domain.achievement.entity.RefAchievement;
import com.ssafy.farmily.domain.achievement.repository.RefAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefAchievementService {

    private final RefAchievementRepository refAchievementRepository;

    /**
     * 전체 업적 목록 조회
     */
    public List<RefAchievement> getAllAchievements() {
        return refAchievementRepository.findAll();
    }

    /**
     * 코드로 업적 조회
     */
    public RefAchievement getAchievementByCode(String code) {
        return refAchievementRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("업적을 찾을 수 없습니다: " + code));
    }
}
