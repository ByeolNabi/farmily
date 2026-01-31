package com.ssafy.farmily.domain.plant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 식물 엔티티
 * ERD의 plant 테이블과 매핑
 */
@Entity
@Table(name = "plant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "users_id", nullable = false)
    private Long userId;

    @Column(name = "ref_plant_species_id")
    private Long refPlantSpeciesId;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(name = "health_status", length = 50)
    private String healthStatus;

    @Column(name = "health_checked_at")
    private LocalDateTime healthCheckedAt;

    @Column(name = "low_temperature", precision = 5, scale = 2)
    private BigDecimal lowTemperature;

    @Column(name = "high_temperature", precision = 5, scale = 2)
    private BigDecimal highTemperature;

    @Column(name = "love_temperature", precision = 5, scale = 2, nullable = false)
    private BigDecimal statusPoint = BigDecimal.ZERO;  // 애착점수 (초기값 0)

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 포인트 추가
     * @param points 추가할 포인트
     */
    public void addPoints(BigDecimal points) {
        this.statusPoint = this.statusPoint.add(points);
        // 최대 100점 제한
        if (this.statusPoint.compareTo(new BigDecimal("100")) > 0) {
            this.statusPoint = new BigDecimal("100");
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.statusPoint == null) {
            this.statusPoint = BigDecimal.ZERO;
        }
    }
}
