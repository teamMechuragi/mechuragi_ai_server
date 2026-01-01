package com.mechuragi.ai.dto;

import com.mechuragi.ai.type.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendationRequest {

    private Long memberId;
    private RecommendationType recommendationType;
    private String name;
    private String description;
    private String reason;
    private String ingredients;
    private String cookingTime;
    private String difficulty;
}
