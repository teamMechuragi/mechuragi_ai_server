package com.mechuragi.ai.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendationResponse {

    private String message;
    private List<FoodRecommendationDto> recommendations;
    private String model;

}
