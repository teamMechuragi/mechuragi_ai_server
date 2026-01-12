package com.mechuragi.ai.dto.external;

import com.mechuragi.ai.type.RecommendationType;
import lombok.*;

import java.util.List;
// 프론트 요청 dto
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendationRequest {

    private RecommendationType type;

    // 컨텍스트 필드들
    private List<String> weatherConditions;
    private String timeOfDay;
    private List<String> ingredients;
    private String feeling;
    private String userMessage;

    // 사용자 취향 필드들
    private String dietStatus;
    private String veganOption;
    private String spiceLevel;
    private List<String> foodTypes;
    private List<String> tastes;
    private List<String> dislikedFoods;

}
