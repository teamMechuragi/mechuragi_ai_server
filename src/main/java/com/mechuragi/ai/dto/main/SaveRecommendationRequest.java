package com.mechuragi.ai.dto.main;

import com.mechuragi.ai.type.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 메인 서버 요청 dto (memberId는 메인 서버에서 JWT로 추출)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendationRequest {

    private RecommendationType recommendationType;
    private String name;
    private String description;
    private String reason;
}
