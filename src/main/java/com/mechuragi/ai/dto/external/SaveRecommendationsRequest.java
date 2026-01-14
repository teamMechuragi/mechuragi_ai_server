package com.mechuragi.ai.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 메인 서버 요청 dto (memberId는 메인 서버에서 JWT로 추출)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendationsRequest {

    private List<SaveRecommendationRequest> recommendations;
}
