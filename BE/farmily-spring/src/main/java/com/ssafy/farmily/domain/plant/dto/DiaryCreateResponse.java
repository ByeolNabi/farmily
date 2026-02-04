package com.ssafy.farmily.domain.plant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 일기 생성 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCreateResponse {

    @JsonProperty("diary_id")
    private Long diaryId;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("happened_at")
    private LocalDateTime happenedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
