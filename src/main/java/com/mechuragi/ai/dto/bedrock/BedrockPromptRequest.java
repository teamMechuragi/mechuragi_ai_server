package com.mechuragi.ai.dto.bedrock;

import com.mechuragi.ai.dto.frontend.FoodPreferenceRequest;
import com.mechuragi.ai.type.RecommendationType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BedrockPromptRequest {

    private RecommendationType type;
    private FoodPreferenceRequest preference;

    // 컨텍스트 (type에 따른 단일 컨텍스트 값)
    private List<String> context;

}
