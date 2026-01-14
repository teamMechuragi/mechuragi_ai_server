package com.mechuragi.ai.dto.bedrock;

import com.mechuragi.ai.type.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
// AI가 생성한 추천 결과 (응답에 포함)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedrockRecommendationResponse {
    private RecommendationType recommendationType;
    private String name;
    private String description;
    private String reason;
    private String ingredients;
    private String cookingTime;
    private String difficulty;
}
