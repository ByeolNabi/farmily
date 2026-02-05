package com.ssafy.farmily.domain.healthlog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 식물 질병 참조 엔티티
 * DB 테이블: ref_plant_disease
 */
@Entity
@Table(name = "ref_plant_disease")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefPlantDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    /** 증상 생김새 설명 */
    @Column(columnDefinition = "TEXT")
    private String symptom;

    /** 치료 기간 (일 단위) */
    @Column
    private Integer duration;

    /** 발생 원인 */
    @Column(columnDefinition = "TEXT")
    private String causation;

    /** 치료 방법 */
    @Column(columnDefinition = "TEXT")
    private String instruction;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
