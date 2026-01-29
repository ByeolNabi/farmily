package com.ssafy.farmily.domain.activity.controller;

import com.ssafy.farmily.domain.activity.dto.ActivityLogRequest;
import com.ssafy.farmily.domain.activity.dto.ActivityLogResponse;
import com.ssafy.farmily.domain.activity.service.PlantActivityLogService;
import com.ssafy.farmily.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class PlantActivityLogController {

    private final PlantActivityLogService plantActivityLogService;
    private final AuthUtil authUtil;

    /**
     * 쓰다듬기 활동 기록
     */
    @PostMapping("/petting")
    public ResponseEntity<ActivityLogResponse> logPetting(
            @RequestBody ActivityLogRequest request) {
        Long userId = authUtil.getCurrentUserId();
        ActivityLogResponse response = plantActivityLogService.logPetting(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 물주기 활동 기록
     */
    @PostMapping("/watering")
    public ResponseEntity<ActivityLogResponse> logWatering(
            @RequestBody ActivityLogRequest request) {
        Long userId = authUtil.getCurrentUserId();
        ActivityLogResponse response = plantActivityLogService.logWatering(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 대화 활동 기록
     */
    @PostMapping("/conversation")
    public ResponseEntity<ActivityLogResponse> logConversation(
            @RequestBody ActivityLogRequest request) {
        Long userId = authUtil.getCurrentUserId();
        ActivityLogResponse response = plantActivityLogService.logConversation(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 칭찬 활동 기록
     */
    @PostMapping("/praise")
    public ResponseEntity<ActivityLogResponse> logPraise(
            @RequestBody ActivityLogRequest request) {
        Long userId = authUtil.getCurrentUserId();
        ActivityLogResponse response = plantActivityLogService.logPraise(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 총 활동 횟수 조회
     */
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalActivityCount(
            @RequestParam Long plantId,
            @RequestParam String type) {
        Long totalCount = plantActivityLogService.getTotalActivityCount(plantId, type);
        return ResponseEntity.ok(totalCount);
    }
}
