package com.ssafy.farmily.domain.plant.dto;

import com.ssafy.farmily.domain.plant.entity.RefPlantSpecies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefPlantSpeciesResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private Integer tempTarget;
    private String tempRange;
    private Integer humidTarget;
    private String humidRange;
    private Integer soilTarget;
    private String soilRange;
    private Integer illuminance;

    public static RefPlantSpeciesResponse from(RefPlantSpecies species) {
        return RefPlantSpeciesResponse.builder()
                .id(species.getId())
                .name(species.getName())
                .imageUrl(species.getImageUrl())
                .tempTarget(species.getTempTarget())
                .tempRange(species.getTempRange())
                .humidTarget(species.getHumidTarget())
                .humidRange(species.getHumidRange())
                .soilTarget(species.getSoilTarget())
                .soilRange(species.getSoilRange())
                .illuminance(species.getIlluminance())
                .build();
    }
}
