package com.ssafy.farmily.domain.plant.service;

import com.ssafy.farmily.domain.plant.dto.SensorLogRequest;
import com.ssafy.farmily.domain.plant.dto.SensorLogResponse;
import com.ssafy.farmily.domain.plant.entity.Plant;
import com.ssafy.farmily.domain.plant.entity.PlantSensorLog;
import com.ssafy.farmily.domain.plant.entity.RefPlantSpecies;
import com.ssafy.farmily.domain.plant.entity.WaterAlertLog;
import com.ssafy.farmily.domain.plant.repository.PlantRepository;
import com.ssafy.farmily.domain.plant.repository.PlantSensorLogRepository;
import com.ssafy.farmily.domain.plant.repository.RefPlantSpeciesRepository;
import com.ssafy.farmily.domain.plant.repository.WaterAlertLogRepository;
import com.ssafy.farmily.domain.user.entity.User;
import com.ssafy.farmily.domain.user.repository.UserRepository;
import com.ssafy.farmily.global.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 센서 로그 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensorLogService {

    private final PlantSensorLogRepository sensorLogRepository;
    private final WaterAlertLogRepository alertLogRepository;
    private final PlantRepository plantRepository;
    private final RefPlantSpeciesRepository speciesRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    private static final LocalTime ALERT_START_TIME = LocalTime.of(8, 0);
    private static final LocalTime ALERT_END_TIME = LocalTime.of(22, 0);

    /**
     * 센서 데이터 저장 및 물주기 알림 체크
     */
    @Transactional
    public SensorLogResponse saveSensorLog(Long plantId, SensorLogRequest request) {
        // 1. 센서 데이터 저장
        PlantSensorLog sensorLog = PlantSensorLog.builder()
                .plantId(plantId)
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .soilMoisture(request.getSoilMoisture())
                .illuminance(request.getIlluminance())
                .build();

        PlantSensorLog saved = sensorLogRepository.save(sensorLog);
        log.info("Saved sensor log for plant {}: temp={}, humidity={}, soil={}, illuminance={}",
                plantId, request.getTemperature(), request.getHumidity(),
                request.getSoilMoisture(), request.getIlluminance());

        // 2. 물주기 알림 체크
        checkAndSendWaterAlert(plantId, request.getSoilMoisture());

        return SensorLogResponse.builder()
                .id(saved.getId())
                .plantId(saved.getPlantId())
                .temperature(saved.getTemperature())
                .humidity(saved.getHumidity())
                .soilMoisture(saved.getSoilMoisture())
                .illuminance(saved.getIlluminance())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /**
     * 물주기 알림 체크 및 발송
     */
    private void checkAndSendWaterAlert(Long plantId, BigDecimal soilMoisture) {
        // 1. 시간 체크 (08:00 ~ 22:00만 알림)
        LocalTime now = LocalTime.now();
        if (now.isBefore(ALERT_START_TIME) || now.isAfter(ALERT_END_TIME)) {
            log.info("Outside alert time range (08:00-22:00), skipping alert");
            return;
        }

        // 2. 식물 정보 조회
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("Plant not found: " + plantId));

        // 3. 식물 종 정보 조회 (최적 토양 수분 범위)
        RefPlantSpecies species = speciesRepository.findById(plant.getRefPlantSpeciesId())
                .orElseThrow(() -> new IllegalArgumentException("Plant species not found: " + plant.getRefPlantSpeciesId()));

        // 4. soil_range 파싱 (예: "[40,71)")
        String soilRange = species.getSoilRange();
        if (soilRange == null || soilRange.isBlank()) {
            log.warn("Soil range not defined for species {}", species.getId());
            return;
        }

        int[] range = parseSoilRange(soilRange);
        int lowerBound = range[0];
        int upperBound = range[1];

        // 5. 토양 수분 상태 판단 및 알림 발송
        if (soilMoisture.compareTo(BigDecimal.valueOf(lowerBound)) < 0) {
            // 부족: 매번 알림
            sendWaterAlert(plant, "LOW", plant.getNickname() + "이 물이 필요해요 💧");
        } else if (soilMoisture.compareTo(BigDecimal.valueOf(upperBound)) >= 0) {
            // 과다: 하루 1번만 알림
            LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
            boolean alreadySentToday = alertLogRepository.existsByPlantIdAndAlertTypeAndLastAlertAtAfter(
                    plantId, "HIGH", todayStart);

            if (!alreadySentToday) {
                sendWaterAlert(plant, "HIGH", plant.getNickname() + "의 토양이 너무 습해요 ⚠️");
            } else {
                log.info("HIGH alert already sent today for plant {}", plantId);
            }
        } else {
            log.info("Soil moisture is within normal range for plant {}", plantId);
        }
    }

    /**
     * soil_range 문자열 파싱 (예: "[40,71)" -> [40, 71])
     */
    private int[] parseSoilRange(String soilRange) {
        try {
            String cleaned = soilRange.replaceAll("[\\[\\]\\(\\)]", "");
            String[] parts = cleaned.split(",");
            return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
        } catch (Exception e) {
            log.error("Failed to parse soil range: {}", soilRange, e);
            return new int[]{0, 100}; // 기본값
        }
    }

    /**
     * 물주기 알림 발송
     */
    private void sendWaterAlert(Plant plant, String alertType, String message) {
        // 1. 사용자 FCM 토큰 조회
        User user = userRepository.findById(plant.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + plant.getUserId()));

        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM token not found for user {}", user.getId());
            return;
        }

        // 2. FCM 푸시 알림 발송
        String title = alertType.equals("LOW") ? "물 부족 알림" : "물 과다 알림";
        fcmService.sendPushNotification(fcmToken, title, message);

        // 3. 알림 이력 저장
        WaterAlertLog alertLog = WaterAlertLog.builder()
                .plantId(plant.getId())
                .alertType(alertType)
                .lastAlertAt(LocalDateTime.now())
                .build();
        alertLogRepository.save(alertLog);

        log.info("Sent {} alert for plant {}: {}", alertType, plant.getId(), message);
    }
}
