package com.mechuragi.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendationsRequest {

    private Long memberId;
    private List<SaveRecommendationRequest> recommendations;
}
