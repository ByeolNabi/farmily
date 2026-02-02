package com.ssafy.farmily.domain.plant.controller;

import com.ssafy.farmily.domain.plant.dto.PointAction;
import com.ssafy.farmily.domain.plant.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 포인트 관리 컨트롤러
 * FastAPI로부터 활동 신호를 받아 포인트를 지급
 */
@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 포인트 지급 API
     * @param plantId 식물 ID
     * @param request action 필드 포함
     * @return 성공 메시지
     */
    @PostMapping("/{plantId}/points")
    public Map<String, String> earnPoint(
            @PathVariable Long plantId,
            @RequestBody EarnPointRequest request
    ) {
        PointAction action = PointAction.valueOf(request.action().toUpperCase());
        pointService.earnPoint(plantId, action);
        return Map.of("message", "포인트 지급 완료");
    }

    /**
     * 현재 포인트 조회 API
     * @param plantId 식물 ID
     * @return 현재 포인트
     */
    @GetMapping("/{plantId}/points")
    public Map<String, BigDecimal> getCurrentPoint(@PathVariable Long plantId) {
        BigDecimal currentPoint = pointService.getCurrentPoint(plantId);
        return Map.of("loveTemperature", currentPoint);
    }


    // Request DTO
    public record EarnPointRequest(String action) {}
}
