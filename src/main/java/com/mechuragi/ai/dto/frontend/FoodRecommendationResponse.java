package com.mechuragi.ai.dto.frontend;

import com.mechuragi.ai.dto.bedrock.BedrockRecommendationResponse;
import lombok.*;

import java.util.List;
// 프론트 응답 dto
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendationResponse {

    private List<BedrockRecommendationResponse> recommendations;
    private String model;

}
