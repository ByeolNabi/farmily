package com.ssafy.farmily.domain.plantdiary.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantDiaryResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
