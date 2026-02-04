package com.ssafy.farmily.domain.timelapse.service;

import com.ssafy.farmily.domain.timelapse.dto.TimelapseCreateResponse;
import com.ssafy.farmily.domain.timelapse.dto.TimelapseListResponse;
import com.ssafy.farmily.domain.timelapse.dto.TimelapsePhotoResponse;
import com.ssafy.farmily.domain.timelapse.entity.PlantTimelapse;
import com.ssafy.farmily.domain.timelapse.repository.PlantTimelapseRepository;
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
 * 타임랩스 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TimelapseService {

    private final PlantTimelapseRepository timelapseRepository;
    private final S3Service s3Service;

    private static final String TIMELAPSE_DIRECTORY = "timelapse";

    /**
     * 타임랩스 목록 조회
     */
    public TimelapseListResponse getTimelapses(Long plantId) {
        List<PlantTimelapse> timelapses = timelapseRepository.findByPlantIdOrderByCreatedAtAsc(plantId);

        List<TimelapsePhotoResponse> photos = timelapses.stream()
                .map(this::toPhotoResponse)
                .collect(Collectors.toList());

        return TimelapseListResponse.builder()
                .totalFrames(photos.size())
                .photos(photos)
                .build();
    }

    /**
     * 타임랩스 생성
     */
    @Transactional
    public TimelapseCreateResponse createTimelapse(Long plantId, MultipartFile image, LocalDateTime createdAt) {
        // 1. S3에 이미지 업로드
        String imageUrl = s3Service.uploadFile(image, TIMELAPSE_DIRECTORY);

        // 2. DB 저장
        PlantTimelapse timelapse = PlantTimelapse.builder()
                .plantId(plantId)
                .imageUrl(imageUrl)
                .createdAt(createdAt != null ? createdAt : LocalDateTime.now())
                .build();

        PlantTimelapse saved = timelapseRepository.save(timelapse);

        log.info("타임랩스 생성 완료: photoId={}, plantId={}", saved.getId(), plantId);

        return TimelapseCreateResponse.builder()
                .photoId(saved.getId())
                .imageUrl(saved.getImageUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /**
     * 타임랩스 삭제
     */
    @Transactional
    public void deleteTimelapse(Long photoId) {
        PlantTimelapse timelapse = timelapseRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 사진을 찾을 수 없습니다."));

        // 1. S3 파일 삭제 시도
        try {
            s3Service.deleteFile(timelapse.getImageUrl());
        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패 (DB 삭제는 진행): {}", e.getMessage());
        }

        // 2. DB 삭제
        timelapseRepository.delete(timelapse);

        log.info("타임랩스 삭제 완료: photoId={}", photoId);
    }

    private TimelapsePhotoResponse toPhotoResponse(PlantTimelapse timelapse) {
        return TimelapsePhotoResponse.builder()
                .photoId(timelapse.getId())
                .imageUrl(timelapse.getImageUrl())
                .createdAt(timelapse.getCreatedAt())
                .build();
    }
}
