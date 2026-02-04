package com.ssafy.farmily.domain.plant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 물주기 알림 로그 엔티티
 * 알림 발송 이력 추적 (과다 알림 하루 1번 제한용)
 */
@Entity
@Table(name = "water_alert_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WaterAlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(name = "alert_type", length = 10, nullable = false)
    private String alertType; // "LOW" or "HIGH"

    @Column(name = "last_alert_at", nullable = false)
    private LocalDateTime lastAlertAt;

    @PrePersist
    protected void onCreate() {
        if (this.lastAlertAt == null) {
            this.lastAlertAt = LocalDateTime.now();
        }
    }
}
