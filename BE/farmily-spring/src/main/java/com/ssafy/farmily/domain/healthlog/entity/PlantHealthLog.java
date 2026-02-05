package com.ssafy.farmily.domain.healthlog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 식물 건강 로그 엔티티
 * DB 테이블: plant_health_logs
 * YOLO 진단 결과를 저장
 */
@Entity
@Table(name = "plant_health_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlantHealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_timelapse_id", nullable = false)
    private Long plantTimelapseId;

    @Column(name = "ref_plant_disease_id", nullable = false)
    private Long refPlantDiseaseId;

    /**
     * YOLO 바운딩 박스 좌표 (JSONB)
     * 예: {"x": 100, "y": 200, "width": 50, "height": 50}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "bbox", nullable = false, columnDefinition = "jsonb")
    private String bbox;

    /**
     * 신뢰도 (0~100)
     */
    @Column(name = "confidence", precision = 5, scale = 2)
    private BigDecimal confidence;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // 관계 설정 (조회 시 필요할 경우 사용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_timelapse_id", insertable = false, updatable = false)
    private com.ssafy.farmily.domain.timelapse.entity.PlantTimelapse plantTimelapse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_plant_disease_id", insertable = false, updatable = false)
    private RefPlantDisease refPlantDisease;
}
