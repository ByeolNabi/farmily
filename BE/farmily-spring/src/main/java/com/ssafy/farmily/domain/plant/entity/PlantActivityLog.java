package com.ssafy.farmily.domain.plant.entity;

import com.ssafy.farmily.domain.plant.dto.ActivityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 식물 활동 로그 엔티티
 * ERD의 plant_activity_log 테이블과 매핑
 */
@Entity
@Table(name = "plant_activity_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlantActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ActivityType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
