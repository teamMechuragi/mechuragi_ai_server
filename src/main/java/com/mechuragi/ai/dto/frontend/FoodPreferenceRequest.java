package com.mechuragi.ai.dto.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
//  요청에서 추출한 사용자 취향 정보 (프롬프트 생성용)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodPreferenceRequest {
    private String dietStatus;
    private String veganOption;
    private String spiceLevel;
    private List<String> foodTypes;
    private List<String> tastes;
    private List<String> dislikedFoods;
}
