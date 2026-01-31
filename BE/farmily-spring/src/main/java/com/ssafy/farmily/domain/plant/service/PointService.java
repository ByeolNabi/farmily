package com.ssafy.farmily.domain.plant.service;

import com.ssafy.farmily.domain.plant.dto.PointAction;
import com.ssafy.farmily.domain.plant.entity.Plant;
import com.ssafy.farmily.domain.plant.entity.PlantActivityLog;
import com.ssafy.farmily.domain.plant.repository.PlantActivityLogRepository;
import com.ssafy.farmily.domain.plant.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 포인트 지급 서비스
 * 체감형 포인트 계산 및 일일 제한 관리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PointService {

    private final PlantRepository plantRepository;
    private final PlantActivityLogRepository activityLogRepository;

    private static final BigDecimal MAX_POINT = new BigDecimal("100");
    private static final BigDecimal MIN_WEIGHT = new BigDecimal("0.1");  // 최소 가중치 10%

    /**
     * 포인트 지급
     * @param plantId 식물 ID
     * @param action 수행한 행동
     * @throws IllegalArgumentException 식물을 찾을 수 없거나 일일 제한 초과 시
     */
    @Transactional
    public void earnPoint(Long plantId, PointAction action) {
        // 1. 식물 조회
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("식물을 찾을 수 없습니다. ID: " + plantId));

        // 2. 일일 제한 체크
        LocalDate today = LocalDate.now();
        long todayCount = activityLogRepository.countByPlantIdAndTypeAndDate(
                plantId, action.getActivityType(), today
        );

        if (todayCount >= action.getDailyLimit()) {
            throw new IllegalArgumentException(
                    String.format("오늘 %s 활동은 이미 %d회 수행했습니다. (제한: %d회)",
                            action.name(), todayCount, action.getDailyLimit())
            );
        }

        // 3. 현재 포인트 조회
        BigDecimal currentPoint = plant.getStatusPoint();

        // 4. 체감형 포인트 계산
        // 공식: 지급 포인트 = 기본 포인트 × max(0.1, 1 - 현재점수/100)
        BigDecimal weight = BigDecimal.ONE.subtract(
                currentPoint.divide(MAX_POINT, 10, RoundingMode.HALF_UP)
        );
        weight = weight.max(MIN_WEIGHT);  // 최소 10% 보장

        BigDecimal earnedPoint = action.getBasePoint()
                .multiply(weight)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("포인트 계산 - 식물ID: {}, 행동: {}, 현재점수: {}, 가중치: {}, 지급점수: {}",
                plantId, action.name(), currentPoint, weight, earnedPoint);

        // 5. 포인트 추가 (Plant 엔티티의 addPoints 메서드가 100점 제한 처리)
        plant.addPoints(earnedPoint);

        // 6. 활동 로그 기록
        PlantActivityLog activityLog = PlantActivityLog.builder()
                .plantId(plantId)
                .type(action.getActivityType())
                .build();
        activityLogRepository.save(activityLog);

        log.info("포인트 지급 완료 - 식물ID: {}, 최종점수: {}", plantId, plant.getStatusPoint());
    }

    /**
     * 식물의 현재 포인트 조회
     * @param plantId 식물 ID
     * @return 현재 포인트
     */
    public BigDecimal getCurrentPoint(Long plantId) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("식물을 찾을 수 없습니다. ID: " + plantId));
        return plant.getStatusPoint();
    }
}
