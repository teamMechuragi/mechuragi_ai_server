package com.mechuragi.ai.dto.frontend;

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

    // 컨텍스트 (type에 따른 단일 컨텍스트 값)
    // WEATHER: ["맑음", "더움"], TIME: ["아침"], INGREDIENTS: ["계란", "김치"] ,FEELING: ["피곤함"], CONVERSATION: ["오늘 뭐 먹을까?"]
    private List<String> context;

    // 사용자 취향 필드들
    private String dietStatus;
    private String veganOption;
    private String spiceLevel;
    private List<String> foodTypes;
    private List<String> tastes;
    private List<String> dislikedFoods;

}
