package com.ssafy.farmily.domain.plant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 일기 수정 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryUpdateResponse {

    private Long id;

    private String content;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("happened_at")
    private LocalDateTime happenedAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
