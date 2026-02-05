package com.ssafy.farmily.domain.timelapse.controller;

import com.ssafy.farmily.domain.timelapse.dto.TimelapseCreateResponse;
import com.ssafy.farmily.domain.timelapse.dto.TimelapseListResponse;
import com.ssafy.farmily.domain.timelapse.service.TimelapseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * 타임랩스 컨트롤러
 */
@RestController
@RequestMapping("/timelapse")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Timelapse", description = "타임랩스 API")
public class TimelapseController {

    private final TimelapseService timelapseService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 타임랩스 목록 조회
     */
    @GetMapping
    @Operation(summary = "타임랩스 목록 조회", description = "특정 식물의 타임랩스 사진 목록을 조회합니다.")
    public ResponseEntity<TimelapseListResponse> getTimelapseList(
            @Parameter(description = "식물 ID", required = true) @RequestParam("plant_id") Long plantId) {
        TimelapseListResponse response = timelapseService.getTimelapses(plantId);

        if (response.getPhotos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "재생 가능한 타임랩스 데이터가 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 타임랩스 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "타임랩스 사진 업로드", description = "타임랩스 사진을 업로드합니다.")
    public ResponseEntity<TimelapseCreateResponse> createTimelapse(
            @Parameter(description = "식물 ID", required = true) @RequestParam("plant_id") Long plantId,

            @Parameter(description = "촬영 시점 (ISO 8601)") @RequestParam(value = "created_at", required = false) String createdAtStr,

            @Parameter(description = "이미지 파일", required = true) @RequestPart("image") MultipartFile image) {
        // 1. 파일 크기 검증
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "이미지 파일이 너무 큽니다. (최대 10MB)");
        }

        // 2. 시간 파싱
        LocalDateTime createdAt = null;
        if (createdAtStr != null && !createdAtStr.isBlank()) {
            try {
                createdAt = LocalDateTime.parse(createdAtStr);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "촬영 시간(created_at) 정보가 올바르지 않습니다.");
            }
        }

        // 3. 서비스 호출
        try {
            TimelapseCreateResponse response = timelapseService.createTimelapse(plantId, image, createdAt);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("타임랩스 생성 실패: plantId={}, fileName={}, error={}",
                    plantId, image.getOriginalFilename(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "이미지 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 타임랩스 삭제
     */
    @DeleteMapping("/{photoId}")
    @Operation(summary = "타임랩스 사진 삭제", description = "타임랩스 사진을 삭제합니다.")
    public ResponseEntity<Void> deleteTimelapse(
            @Parameter(description = "사진 ID", required = true) @PathVariable Long photoId) {
        try {
            timelapseService.deleteTimelapse(photoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
