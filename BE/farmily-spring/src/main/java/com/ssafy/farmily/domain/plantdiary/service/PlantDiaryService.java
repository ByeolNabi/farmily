package com.ssafy.farmily.domain.plantdiary.service;

import com.ssafy.farmily.domain.plantdiary.dto.PlantDiaryListResponse;
import com.ssafy.farmily.domain.plantdiary.dto.PlantDiaryResponse;
import com.ssafy.farmily.domain.plantdiary.entity.PlantDiary;
import com.ssafy.farmily.domain.plantdiary.repository.PlantDiaryRepository;
import com.ssafy.farmily.domain.activity.dto.ActivityLogRequest;
import com.ssafy.farmily.domain.activity.service.PlantActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantDiaryService {

    private final PlantDiaryRepository plantDiaryRepository;
    private final PlantActivityLogService plantActivityLogService;
    // private final S3Service s3Service; // TODO: S3 서비스 구현 후 주입

    /**
     * 전체 일기 목록 조회 (recorded_at 최신순)
     */
    @Transactional(readOnly = true)
    public PlantDiaryListResponse getAllDiaries(Long userId, Long plantId) {
        List<PlantDiary> diaries = plantDiaryRepository.findByUserIdAndPlantIdOrderByRecordedAtDesc(userId, plantId);
        
        List<PlantDiaryResponse> diaryResponses = diaries.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PlantDiaryListResponse.builder()
                .totalCount(diaryResponses.size())
                .diaries(diaryResponses)
                .build();
    }

    /**
     * 일기 작성
     */
    @Transactional
    public PlantDiaryResponse createDiary(Long userId, Long plantId, String title, String content, LocalDateTime recordedAt, MultipartFile image) {
        String imageUrl = null;
        
        if (image != null && !image.isEmpty()) {
            // TODO: S3 업로드
            // imageUrl = s3Service.uploadFile(image, "diary/" + userId + "/" + plantId);
            imageUrl = "placeholder-image-url"; // 임시
        }

        PlantDiary diary = PlantDiary.builder()
                .userId(userId)
                .plantId(plantId)
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .recordedAt(recordedAt)
                .build();

        PlantDiary saved = plantDiaryRepository.save(diary);

        // 일기 작성 활동 기록
        ActivityLogRequest request = ActivityLogRequest.builder()
                .plantId(plantId)
                .build();
        plantActivityLogService.logDiary(userId, request);

        log.info("Diary created - userId: {}, plantId: {}, diaryId: {}, recordedAt: {}", 
                userId, plantId, saved.getId(), recordedAt);

        return toResponse(saved);
    }

    /**
     * 일기 상세 조회
     */
    @Transactional(readOnly = true)
    public PlantDiaryResponse getDiaryById(Long diaryId) {
        PlantDiary diary = plantDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));
        return toResponse(diary);
    }

    /**
     * 일기 수정 (PATCH - 부분 수정)
     */
    @Transactional
    public PlantDiaryResponse updateDiary(Long userId, Long diaryId, String title, String content, LocalDateTime recordedAt, MultipartFile image) {
        PlantDiary diary = plantDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        if (!diary.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 일기만 수정할 수 있습니다.");
        }

        // 부분 수정: 필드가 제공된 경우에만 업데이트
        if (title != null) {
            diary.updateTitle(title);
        }

        if (content != null) {
            diary.updateContent(content);
        }

        if (recordedAt != null) {
            diary.updateRecordedAt(recordedAt);
        }

        if (image != null && !image.isEmpty()) {
            // TODO: S3 업로드
            String imageUrl = "placeholder-image-url";
            diary.updateImageUrl(imageUrl);
        }

        PlantDiary updated = plantDiaryRepository.save(diary);
        return toResponse(updated);
    }

    /**
     * 일기 삭제
     */
    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        PlantDiary diary = plantDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        if (!diary.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 일기만 삭제할 수 있습니다.");
        }

        // TODO: S3에서 이미지 삭제
        // if (diary.getImageUrl() != null) {
        //     s3Service.deleteFile(diary.getImageUrl());
        // }

        plantDiaryRepository.delete(diary);
        log.info("Diary deleted - userId: {}, diaryId: {}", userId, diaryId);
    }

    /**
     * Entity → Response DTO 변환
     */
    private PlantDiaryResponse toResponse(PlantDiary diary) {
        return PlantDiaryResponse.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .imageUrl(diary.getImageUrl())
                .recordedAt(diary.getRecordedAt())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }
}
