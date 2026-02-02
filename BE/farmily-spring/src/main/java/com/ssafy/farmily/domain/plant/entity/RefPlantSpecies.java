package com.ssafy.farmily.domain.plant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 식물 종(도감) 엔티티
 * ERD의 ref_plant_species 테이블과 매핑
 */
@Entity
@Table(name = "ref_plant_species")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefPlantSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "temp_target")
    private Integer tempTarget;

    @Column(name = "temp_range", length = 50)
    private String tempRange;

    @Column(name = "humid_target")
    private Integer humidTarget;

    @Column(name = "humid_range", length = 50)
    private String humidRange;

    @Column(name = "soil_target")
    private Integer soilTarget;

    @Column(name = "soil_range", length = 50)
    private String soilRange;

    @Column(name = "illuminance")
    private Integer illuminance;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
