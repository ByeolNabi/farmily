package com.ssafy.farmily.domain.plant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 식물 일기 엔티티
 * ERD의 plant_diary 테이블과 매핑
 */
@Entity
@Table(name = "plant_diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlantDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * 실제 사건이 일어난 시간 (달력 표시용)
     * 사용자가 선택한 일기 날짜
     */
    @Column(name = "happened_at", nullable = false)
    private LocalDateTime happenedAt;

    /**
     * 수정한 시간
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * DB에 데이터가 생성된 시간 (시스템용)
     * 실제로 기록을 저장한 시스템 시간
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 일기 내용 수정
     */
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 일기 이미지 수정
     */
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 일기 날짜 수정
     */
    public void updateHappenedAt(LocalDateTime happenedAt) {
        this.happenedAt = happenedAt;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 일기 전체 수정
     */
    public void update(String content, String imageUrl, LocalDateTime happenedAt) {
        if (content != null) {
            this.content = content;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        if (happenedAt != null) {
            this.happenedAt = happenedAt;
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.happenedAt == null) {
            this.happenedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
