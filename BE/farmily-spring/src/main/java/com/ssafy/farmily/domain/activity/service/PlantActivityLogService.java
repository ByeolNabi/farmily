package com.ssafy.farmily.domain.activity.service;

import com.ssafy.farmily.domain.activity.dto.ActivityLogRequest;
import com.ssafy.farmily.domain.activity.dto.ActivityLogResponse;
import com.ssafy.farmily.domain.activity.entity.PlantActivityLog;
import com.ssafy.farmily.domain.activity.entity.PlantActivityCounts;
import com.ssafy.farmily.domain.activity.repository.PlantActivityLogRepository;
import com.ssafy.farmily.domain.activity.repository.PlantActivityCountsRepository;
import com.ssafy.farmily.domain.achievement.service.PlantAchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantActivityLogService {

    private final PlantActivityLogRepository plantActivityLogRepository;
    private final PlantActivityCountsRepository plantActivityCountsRepository;
    private final StringRedisTemplate redisTemplate;
    private final PlantAchievementService plantAchievementService;

    private static final String REDIS_KEY_PREFIX = "activity_limit:";

    // 활동 타입 상수 (SQL ENUM과 일치)
    public static final String TYPE_PETTING = "petting";
    public static final String TYPE_WATERING = "watering";
    public static final String TYPE_CONVERSATION = "talking";  // SQL: talking
    public static final String TYPE_PRAISE = "praising";       // SQL: praising
    public static final String TYPE_DIARY = "diary";

    // 일일 제한
    private static final int LIMIT_PETTING = 3;
    private static final int LIMIT_WATERING = 1;  // 3일에 1회
    private static final int LIMIT_CONVERSATION = 1;
    private static final int LIMIT_PRAISE = 1;
    private static final int LIMIT_DIARY = 1;

    @Transactional
    public ActivityLogResponse logPetting(Long userId, ActivityLogRequest request) {
        return logActivity(userId, request.getPlantId(), TYPE_PETTING, LIMIT_PETTING);
    }

    @Transactional
    public ActivityLogResponse logWatering(Long userId, ActivityLogRequest request) {
        // 물주기는 3일에 한 번 체크
        String wateringKey = getRedisKey(userId, request.getPlantId(), TYPE_WATERING);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(wateringKey))) {
            Long totalCount = getStatsCount(request.getPlantId(), TYPE_WATERING);
            return ActivityLogResponse.builder()
                    .success(false)
                    .message("물은 3일에 한 번만 주세요!")
                    .totalCount(totalCount)
                    .build();
        }

        ActivityLogResponse response = logActivity(userId, request.getPlantId(), TYPE_WATERING, LIMIT_WATERING);
        
        if (response.isSuccess()) {
            redisTemplate.opsForValue().set(wateringKey, "1", 3, TimeUnit.DAYS);
        }

        return response;
    }

    @Transactional
    public ActivityLogResponse logConversation(Long userId, ActivityLogRequest request) {
        return logActivity(userId, request.getPlantId(), TYPE_CONVERSATION, LIMIT_CONVERSATION);
    }

    @Transactional
    public ActivityLogResponse logPraise(Long userId, ActivityLogRequest request) {
        return logActivity(userId, request.getPlantId(), TYPE_PRAISE, LIMIT_PRAISE);
    }

    @Transactional
    public ActivityLogResponse logDiary(Long userId, ActivityLogRequest request) {
        return logActivity(userId, request.getPlantId(), TYPE_DIARY, LIMIT_DIARY);
    }

    private ActivityLogResponse logActivity(Long userId, Long plantId, String type, int dailyLimit) {
        if (isDailyLimitReached(userId, plantId, type, dailyLimit)) {
            Long totalCount = getStatsCount(plantId, type);
            return ActivityLogResponse.builder()
                    .success(false)
                    .message("오늘은 이미 기록했어요!")
                    .totalCount(totalCount)
                    .build();
        }

        PlantActivityLog log = PlantActivityLog.builder()
                .plantId(plantId)
                .type(type)
                .build();

        plantActivityLogRepository.save(log);
        incrementDailyCount(userId, plantId, type);

        // 카운트 테이블 업데이트
        updateStats(plantId, type);

        // 업적 체크
        plantAchievementService.checkAndUnlockAchievements(userId, plantId);

        // 카운트 테이블에서 총 카운트 조회 (빠름!)
        Long totalCount = getStatsCount(plantId, type);

        log.info("Activity logged - userId: {}, plantId: {}, type: {}, total: {}",
                userId, plantId, type, totalCount);

        return ActivityLogResponse.builder()
                .success(true)
                .message("활동이 기록되었어요!")
                .totalCount(totalCount)
                .build();
    }

    /**
     * 카운트 테이블 업데이트 (트랜잭션 내에서 자동 실행)
     */
    private void updateStats(Long plantId, String type) {
        PlantActivityCounts counts = plantActivityCountsRepository
                .findByPlantIdAndActivityType(plantId, type)
                .orElse(null);

        if (counts == null) {
            // 처음 기록하는 경우 새로 생성
            counts = PlantActivityCounts.builder()
                    .plantId(plantId)
                    .activityType(type)
                    .totalCount(1)
                    .build();
            plantActivityCountsRepository.save(counts);
        } else {
            // 기존 레코드 카운트 증가
            counts.incrementCount();
            plantActivityCountsRepository.save(counts);
        }
    }

    /**
     * 카운트 테이블에서 총 카운트 조회 (고속)
     */
    private Long getStatsCount(Long plantId, String type) {
        return plantActivityCountsRepository
                .findByPlantIdAndActivityType(plantId, type)
                .map(counts -> counts.getTotalCount().longValue())
                .orElse(0L);
    }

    private boolean isDailyLimitReached(Long userId, Long plantId, String type, int dailyLimit) {
        String key = getRedisKey(userId, plantId, type);
        String countStr = redisTemplate.opsForValue().get(key);
        
        if (countStr == null) {
            return false;
        }

        int currentCount = Integer.parseInt(countStr);
        return currentCount >= dailyLimit;
    }

    private void incrementDailyCount(Long userId, Long plantId, String type) {
        String key = getRedisKey(userId, plantId, type);
        redisTemplate.opsForValue().increment(key);
        
        long secondsUntilMidnight = Duration.between(
                LocalDateTime.now(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()
        ).getSeconds();
        
        redisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
    }

    private String getRedisKey(Long userId, Long plantId, String type) {
        String today = LocalDate.now().toString();
        return String.format("%s%d:%d:%s:%s", REDIS_KEY_PREFIX, userId, plantId, type, today);
    }

    @Transactional(readOnly = true)
    public Long getTotalActivityCount(Long plantId, String type) {
        // 카운트 테이블에서 조회 (빠름!)
        return getStatsCount(plantId, type);
    }
}
