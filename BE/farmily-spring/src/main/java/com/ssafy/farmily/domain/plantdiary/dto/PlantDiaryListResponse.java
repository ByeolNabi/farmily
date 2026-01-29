package com.ssafy.farmily.domain.plantdiary.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantDiaryListResponse {
    private Integer totalCount;
    private List<PlantDiaryResponse> diaries;
}
