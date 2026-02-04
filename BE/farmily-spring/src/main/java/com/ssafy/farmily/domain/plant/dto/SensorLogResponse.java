package com.ssafy.farmily.domain.plant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 센서 로그 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class SensorLogResponse {

    private Long id;
    private Long plantId;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal soilMoisture;
    private BigDecimal illuminance;
    private LocalDateTime createdAt;
}
