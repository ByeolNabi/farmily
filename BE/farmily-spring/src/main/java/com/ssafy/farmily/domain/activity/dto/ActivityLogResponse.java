package com.ssafy.farmily.domain.activity.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogResponse {
    private boolean success;
    private String message;
    private Long totalCount;  // 총 활동 횟수
}
