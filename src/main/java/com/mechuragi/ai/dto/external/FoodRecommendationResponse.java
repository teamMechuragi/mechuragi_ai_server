package com.mechuragi.ai.dto.external;

import com.mechuragi.ai.dto.internal.FoodRecommendationDto;
import lombok.*;

import java.util.List;
// 프론트 응답 dto
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendationResponse {

    private String message;
    private List<FoodRecommendationDto> recommendations;
    private String model;

}
