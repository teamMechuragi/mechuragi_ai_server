package com.mechuragi.ai.dto.main;

import com.mechuragi.ai.dto.frontend.FoodPreferenceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 메인 서버 요청 dto
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendationsRequest {

    private List<String> context;
    private FoodPreferenceRequest preference;
    private List<SaveRecommendationRequest> recommendations;
}
