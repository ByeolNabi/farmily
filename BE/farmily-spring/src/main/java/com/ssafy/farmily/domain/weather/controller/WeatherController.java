package com.ssafy.farmily.domain.weather.controller;

import com.ssafy.farmily.domain.weather.dto.WeatherResponse;
import com.ssafy.farmily.domain.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Weather API", description = "날씨 정보 조회 및 로봇 전송")
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "날씨 조회 및 메시지 전송", description = "위도/경도 기반으로 날씨를 조회하고 로봇(MQTT)으로 전송합니다. (파라미터 생략 시 서울 기준)")
    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon
    ) {
        // 수동 호출은 특정 식물 ID가 없으므로 null 전달 (공통 토픽으로 전송됨)
        WeatherResponse response = weatherService.getCurrentWeather(lat, lon, null);
        return ResponseEntity.ok(response);
    }
}
