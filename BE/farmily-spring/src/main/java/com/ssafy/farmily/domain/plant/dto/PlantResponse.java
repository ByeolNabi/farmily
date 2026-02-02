package com.ssafy.farmily.domain.plant.dto;

import com.ssafy.farmily.domain.plant.entity.Plant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PlantResponse {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private BigDecimal loveTemperature;
    private boolean isActive;
    private LocalDateTime startedAt;
    private RefPlantSpeciesResponse species; // 종 정보 포함

    public static PlantResponse from(Plant plant, RefPlantSpeciesResponse speciesResponse) {
        return PlantResponse.builder()
                .id(plant.getId())
                .nickname(plant.getNickname())
                .profileImageUrl(plant.getProfileImageUrl())
                .loveTemperature(plant.getLoveTemperature())
                .isActive(plant.isActive())
                .startedAt(plant.getStartedAt())
                .species(speciesResponse)
                .build();
    }
}
