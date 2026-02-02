package com.ssafy.farmily.domain.plant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlantRegistRequest {
    private Long speciesId;
    private String nickname;
}
