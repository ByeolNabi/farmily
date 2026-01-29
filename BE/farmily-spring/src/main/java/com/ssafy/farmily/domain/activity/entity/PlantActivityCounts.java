package com.ssafy.farmily.domain.activity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plant_activity_counts", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_plant_activity_type", 
                           columnNames = {"plant_id", "activity_type"})
       },
       indexes = {
           @Index(name = "idx_plant_id", columnList = "plant_id")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlantActivityCounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 카운트 증가
     */
    public void incrementCount() {
        this.totalCount++;
    }
}
