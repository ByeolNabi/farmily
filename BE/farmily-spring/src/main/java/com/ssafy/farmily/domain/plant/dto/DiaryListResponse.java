package com.ssafy.farmily.domain.plant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 일기 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryListResponse {

    @JsonProperty("total_count")
    private int totalCount;

    private List<DiaryResponse> diaries;
}
