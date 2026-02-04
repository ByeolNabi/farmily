package com.ssafy.farmily.domain.timelapse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 타임랩스 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelapseListResponse {

    @JsonProperty("total_frames")
    private int totalFrames;

    private List<TimelapsePhotoResponse> photos;
}
