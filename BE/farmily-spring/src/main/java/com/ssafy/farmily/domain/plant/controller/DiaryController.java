package com.ssafy.farmily.domain.plant.controller;

import com.ssafy.farmily.domain.plant.dto.*;
import com.ssafy.farmily.domain.plant.service.DiaryService;
import com.ssafy.farmily.global.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 성장 일기 CRUD 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diaries")
@Tag(name = "Diary", description = "성장 일기 API")
public class DiaryController {

    private final DiaryService diaryService;
    private final AuthUtil authUtil;

    /**
     * 1. 전체 일기 목록 조회 (사용자의 모든 식물)
     * GET /api/v1/diaries
     */
    @GetMapping
    @Operation(summary = "일기 목록 조회", description = "사용자의 모든 일기 또는 특정 식물의 일기를 조회합니다.")
    public ResponseEntity<DiaryListResponse> getDiaries(
            @Parameter(description = "특정 식물 ID (없으면 전체 조회)") @RequestParam(value = "plant_id", required = false) Long plantId) {
        String email = authUtil.getCurrentEmail();

        DiaryListResponse response;
        if (plantId != null) {
            // 특정 식물의 일기 조회
            response = diaryService.getDiariesByPlant(email, plantId);
        } else {
            // 전체 일기 조회
            response = diaryService.getAllDiaries(email);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 2. 일기 생성
     * POST /api/v1/diaries
     */
    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "일기 생성", description = "새로운 일기를 생성합니다. 이미지는 선택사항입니다.")
    public ResponseEntity<DiaryCreateResponse> createDiary(
            @Parameter(description = "식물 사진 파일") @RequestPart(value = "image", required = false) MultipartFile image,

            @Parameter(description = "일기 본문 내용", required = true) @RequestParam("content") String content,

            @Parameter(description = "기록 시점 (ISO 8601, 없으면 현재 시간)") @RequestParam(value = "happened_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime happenedAt,

            @Parameter(description = "식물 ID", required = true) @RequestParam("plant_id") Long plantId) {
        if (happenedAt == null) {
            happenedAt = LocalDateTime.now();
        }
        String email = authUtil.getCurrentEmail();
        DiaryCreateResponse response = diaryService.createDiary(email, plantId, content, happenedAt, image);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. 일기 상세 조회
     * GET /api/v1/diaries/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "일기 상세 조회", description = "특정 일기의 상세 내용을 조회합니다.")
    public ResponseEntity<DiaryResponse> getDiary(
            @Parameter(description = "일기 ID", required = true) @PathVariable("id") Long diaryId) {
        String email = authUtil.getCurrentEmail();
        DiaryResponse response = diaryService.getDiary(email, diaryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 4. 일기 수정
     * PATCH /api/v1/diaries/{id}
     */
    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    @Operation(summary = "일기 수정", description = "일기 내용을 수정합니다. 새 이미지 업로드 시 기존 이미지는 삭제됩니다.")
    public ResponseEntity<DiaryUpdateResponse> updateDiary(
            @Parameter(description = "일기 ID", required = true) @PathVariable("id") Long diaryId,

            @Parameter(description = "새 이미지 파일") @RequestPart(value = "image", required = false) MultipartFile image,

            @Parameter(description = "수정할 본문 내용") @RequestParam(value = "content", required = false) String content,

            @Parameter(description = "수정할 기록 시점 (ISO 8601)") @RequestParam(value = "happened_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime happenedAt) {
        String email = authUtil.getCurrentEmail();
        DiaryUpdateResponse response = diaryService.updateDiary(email, diaryId, content, happenedAt, image);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. 일기 삭제
     * DELETE /api/v1/diaries/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "일기 삭제", description = "일기를 삭제합니다. 저장된 이미지도 함께 삭제됩니다.")
    public ResponseEntity<Void> deleteDiary(
            @Parameter(description = "삭제할 일기 ID", required = true) @PathVariable("id") Long diaryId) {
        String email = authUtil.getCurrentEmail();
        diaryService.deleteDiary(email, diaryId);
        return ResponseEntity.noContent().build();
    }
}
