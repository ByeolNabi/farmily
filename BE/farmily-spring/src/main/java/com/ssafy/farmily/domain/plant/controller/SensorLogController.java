package com.ssafy.farmily.domain.plant.controller;

import com.ssafy.farmily.domain.plant.dto.SensorLogRequest;
import com.ssafy.farmily.domain.plant.dto.SensorLogResponse;
import com.ssafy.farmily.domain.plant.service.SensorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 센서 로그 컨트롤러
 * FastAPI(RPi 5)에서 센서 데이터 수신
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plants")
public class SensorLogController {

    private final SensorLogService sensorLogService;

    /**
     * 센서 데이터 저장
     * X-Device-Key 헤더로 인증 (ApiKeyAuthenticationFilter)
     */
    @PostMapping("/{plantId}/sensor-logs")
    public ResponseEntity<SensorLogResponse> saveSensorLog(
            @PathVariable Long plantId,
            @RequestBody SensorLogRequest request) {
        
        SensorLogResponse response = sensorLogService.saveSensorLog(plantId, request);
        return ResponseEntity.ok(response);
    }
}
