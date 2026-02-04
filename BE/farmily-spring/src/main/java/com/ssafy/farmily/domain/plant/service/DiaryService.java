package com.ssafy.farmily.domain.plant.service;

import com.ssafy.farmily.domain.plant.dto.*;
import com.ssafy.farmily.domain.plant.entity.Plant;
import com.ssafy.farmily.domain.plant.entity.PlantDiary;
import com.ssafy.farmily.domain.plant.repository.PlantDiaryRepository;
import com.ssafy.farmily.domain.plant.repository.PlantRepository;
import com.ssafy.farmily.domain.user.entity.User;
import com.ssafy.farmily.domain.user.repository.UserRepository;
import com.ssafy.farmily.global.exception.AccessDeniedException;
import com.ssafy.farmily.global.exception.ErrorCode;
import com.ssafy.farmily.global.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 일기 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DiaryService {

    private final PlantDiaryRepository diaryRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    private static final String DIARY_IMAGE_DIR = "diary";

    /**
     * 사용자의 모든 일기 조회 (모든 식물)
     */
    public DiaryListResponse getAllDiaries(String email) {
        User user = findUserByEmail(email);

        // 사용자의 모든 식물 ID 조회
        List<Long> plantIds = plantRepository.findByUserId(user.getId())
                .stream()
                .map(Plant::getId)
                .collect(Collectors.toList());

        // 각 식물의 일기를 모아서 반환
        List<DiaryResponse> diaries = plantIds.stream()
                .flatMap(plantId -> diaryRepository.findByPlantIdOrderByHappenedAtDesc(plantId).stream())
                .map(this::toResponse)
                .collect(Collectors.toList());

        return DiaryListResponse.builder()
                .totalCount(diaries.size())
                .diaries(diaries)
                .build();
    }

    /**
     * 특정 식물의 일기 목록 조회
     */
    public DiaryListResponse getDiariesByPlant(String email, Long plantId) {
        validatePlantOwnership(email, plantId);

        List<DiaryResponse> diaries = diaryRepository.findByPlantIdOrderByHappenedAtDesc(plantId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return DiaryListResponse.builder()
                .totalCount(diaries.size())
                .diaries(diaries)
                .build();
    }

    /**
     * 일기 생성
     */
    @Transactional
    public DiaryCreateResponse createDiary(String email, Long plantId, String content,
            LocalDateTime happenedAt, MultipartFile image) {
        validatePlantOwnership(email, plantId);

        // 내용 필수 검증
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("일기 본문 내용은 반드시 입력해야 합니다.");
        }

        // 이미지 업로드
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image, DIARY_IMAGE_DIR);
        }

        // 일기 생성
        PlantDiary diary = PlantDiary.builder()
                .plantId(plantId)
                .content(content)
                .imageUrl(imageUrl)
                .happenedAt(happenedAt != null ? happenedAt : LocalDateTime.now())
                .build();

        PlantDiary saved = diaryRepository.save(diary);

        return DiaryCreateResponse.builder()
                .diaryId(saved.getId())
                .imageUrl(saved.getImageUrl())
                .happenedAt(saved.getHappenedAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /**
     * 일기 상세 조회
     */
    public DiaryResponse getDiary(String email, Long diaryId) {
        PlantDiary diary = findDiaryById(diaryId);
        validatePlantOwnership(email, diary.getPlantId());

        return toResponse(diary);
    }

    /**
     * 일기 수정
     */
    @Transactional
    public DiaryUpdateResponse updateDiary(String email, Long diaryId, String content,
            LocalDateTime happenedAt, MultipartFile image) {
        PlantDiary diary = findDiaryById(diaryId);
        validatePlantOwnership(email, diary.getPlantId());

        // 새 이미지 업로드 시 기존 이미지 삭제
        if (image != null && !image.isEmpty()) {
            // 기존 이미지 삭제
            if (diary.getImageUrl() != null) {
                try {
                    s3Service.deleteFile(diary.getImageUrl());
                } catch (Exception e) {
                    log.warn("기존 이미지 삭제 실패: {}", e.getMessage());
                }
            }
            // 새 이미지 업로드
            String newImageUrl = s3Service.uploadFile(image, DIARY_IMAGE_DIR);
            diary.updateImageUrl(newImageUrl);
        }

        // 내용 수정
        if (content != null && !content.isBlank()) {
            diary.updateContent(content);
        }

        // 날짜 수정
        if (happenedAt != null) {
            diary.updateHappenedAt(happenedAt);
        }

        return DiaryUpdateResponse.builder()
                .id(diary.getId())
                .content(diary.getContent())
                .imageUrl(diary.getImageUrl())
                .happenedAt(diary.getHappenedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }

    /**
     * 일기 삭제
     */
    @Transactional
    public void deleteDiary(String email, Long diaryId) {
        PlantDiary diary = findDiaryById(diaryId);
        validatePlantOwnership(email, diary.getPlantId());

        // 이미지 삭제
        if (diary.getImageUrl() != null) {
            try {
                s3Service.deleteFile(diary.getImageUrl());
            } catch (Exception e) {
                log.warn("이미지 삭제 실패: {}", e.getMessage());
            }
        }

        diaryRepository.delete(diary);
    }

    // ========== Private Methods ==========

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private PlantDiary findDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));
    }

    private void validatePlantOwnership(String email, Long plantId) {
        User user = findUserByEmail(email);
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식물을 찾을 수 없습니다."));

        if (!plant.getUserId().equals(user.getId())) {
            throw new AccessDeniedException(ErrorCode.PLANT_ACCESS_DENIED);
        }
    }

    private DiaryResponse toResponse(PlantDiary diary) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .content(diary.getContent())
                .imageUrl(diary.getImageUrl())
                .happenedAt(diary.getHappenedAt())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }
}
