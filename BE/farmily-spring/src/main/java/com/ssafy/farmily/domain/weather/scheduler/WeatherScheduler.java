package com.ssafy.farmily.domain.weather.scheduler;

import com.ssafy.farmily.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherService weatherService;
    private final com.ssafy.farmily.domain.plant.repository.PlantRepository plantRepository;

    /**
     * 매 시간 정각(0분 0초)마다 날씨 정보 로봇 전송
     */
    @Scheduled(cron = "0 0 * * * *")
    public void sendHourlyWeather() {
        log.info("Hourly Weather Scheduler Started");

        // 1. 활성화된 모든 식물 조회
        java.util.List<com.ssafy.farmily.domain.plant.entity.Plant> activePlants = plantRepository.findByIsActiveTrue();
        
        if (activePlants.isEmpty()) {
            log.info("No active plants found.");
            // (선택) 식물이 하나도 없으면 기본 위치로 한 번 쏴줄 수도 있음
            // weatherService.getCurrentWeather(null, null, null);
        }

        for (com.ssafy.farmily.domain.plant.entity.Plant plant : activePlants) {
            Double lat = null;
            Double lon = null;

            // 2. 위치 정보(stationPoint) 파싱
            // Case A: "37.501,127.039" (단순 문자열)
            // Case B: "POINT(127.039 37.501)" (PostGIS WKT, 경도 위도 순서 확인 필요)
            String point = plant.getStationPoint();
            if (point != null) {
                try {
                    point = point.trim();
                    if (point.toUpperCase().startsWith("POINT")) {
                        // WKT Format: POINT(lon lat) -> 괄호 제거 후 공백으로 분리
                        String wkt = point.substring(point.indexOf("(") + 1, point.indexOf(")"));
                        String[] parts = wkt.trim().split("\\s+");
                        lon = Double.parseDouble(parts[0]);
                        lat = Double.parseDouble(parts[1]);
                    } else if (point.contains(",")) {
                        // Simple Format: lat,lon
                        String[] parts = point.split(",");
                        lat = Double.parseDouble(parts[0].trim());
                        lon = Double.parseDouble(parts[1].trim());
                    }
                } catch (Exception e) {
                    log.warn("Invalid stationPoint format for plant {}: {}", plant.getId(), point);
                }
            }

            // 3. 날씨 조회 및 개별 토픽 전송
            // lat, lon이 null이면 WeatherService에서 기본값(구미 2공장) 사용
            weatherService.getCurrentWeather(lat, lon, plant.getId());
        }
        
        log.info("Hourly Weather Scheduler Finished. Sent updates to {} plants.", activePlants.size());
    }
}
