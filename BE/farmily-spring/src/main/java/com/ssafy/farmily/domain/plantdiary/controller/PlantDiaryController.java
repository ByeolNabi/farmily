package com.ssafy.farmily.domain.plantdiary.controller;

import com.ssafy.farmily.domain.plantdiary.dto.PlantDiaryListResponse;
import com.ssafy.farmily.domain.plantdiary.dto.PlantDiaryResponse;
import com.ssafy.farmily.domain.plantdiary.service.PlantDiaryService;
import com.ssafy.farmily.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class PlantDiaryController {

    private final PlantDiaryService plantDiaryService;
    private final AuthUtil authUtil;

    /**
     * 전체 일기 목록 조회
     */
    @GetMapping
    public ResponseEntity<PlantDiaryListResponse> getAllDiaries(
            @RequestParam Long plantId) {
        Long userId = authUtil.getCurrentUserId();
        PlantDiaryListResponse response = plantDiaryService.getAllDiaries(userId, plantId);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 작성
     */
    @PostMapping
    public ResponseEntity<PlantDiaryResponse> createDiary(
            @RequestParam Long plantId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime recordedAt,
            @RequestParam(required = false) MultipartFile image) {
        Long userId = authUtil.getCurrentUserId();
        PlantDiaryResponse response = plantDiaryService.createDiary(userId, plantId, title, content, recordedAt, image);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlantDiaryResponse> getDiary(@PathVariable Long id) {
        PlantDiaryResponse response = plantDiaryService.getDiaryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 수정 (PATCH)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PlantDiaryResponse> updateDiary(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime recordedAt,
            @RequestParam(required = false) MultipartFile image) {
        Long userId = authUtil.getCurrentUserId();
        PlantDiaryResponse response = plantDiaryService.updateDiary(userId, id, title, content, recordedAt, image);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long id) {
        Long userId = authUtil.getCurrentUserId();
        plantDiaryService.deleteDiary(userId, id);
        return ResponseEntity.noContent().build();
    }
}
