package com.ssafy.farmily.domain.plant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 센서 로그 요청 DTO
 * FastAPI에서 전송하는 1시간 평균 센서 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SensorLogRequest {

    private BigDecimal temperature;

    private BigDecimal humidity;

    private BigDecimal illuminance;

    @JsonProperty("soil_moisture")
    private BigDecimal soilMoisture;
}
