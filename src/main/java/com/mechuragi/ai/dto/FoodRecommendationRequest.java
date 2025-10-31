package com.mechuragi.ai.dto;

import com.mechuragi.ai.type.RecommendationType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendationRequest {

    private RecommendationType type;
    private FoodPreferenceDto preference;

    // 컨텍스트 필드들
    private List<String> weatherConditions;
    private String timeOfDay;
    private List<String> ingredients;
    private String feeling;
    private String userMessage;

}
