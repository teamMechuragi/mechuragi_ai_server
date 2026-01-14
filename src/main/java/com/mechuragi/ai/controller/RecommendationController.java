package com.mechuragi.ai.controller;

import com.mechuragi.ai.dto.external.FoodRecommendationRequest;
import com.mechuragi.ai.dto.external.FoodRecommendationResponse;
import com.mechuragi.ai.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI 음식 추천", description = "사용자 취향 기반 AI 음식 추천 API")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "AI 추천 서비스의 상태를 확인합니다")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "mechuragi-ai-service",
            "version", "1.0.2"
        ));
    }

    @PostMapping
    @Operation(
        summary = "음식 추천 생성",
        description = "사용자의 컨텍스트(날씨, 시간, 재료, 기분, 대화)와 취향 정보를 기반으로 AI가 개인화된 음식을 추천합니다. " +
                     "JWT 토큰은 그대로 메인 서버로 전달되어 사용자 인증에 사용됩니다."
    )
    public ResponseEntity<FoodRecommendationResponse> recommend(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @Valid @RequestBody FoodRecommendationRequest request) {
        try {
            log.info("음식 추천 요청 - 타입: {}", request.getType());

            FoodRecommendationResponse response = recommendationService.generateAndSaveRecommendation(authorization, request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("음식 추천 실패", e);
            FoodRecommendationResponse errorResponse = FoodRecommendationResponse.builder()
                .message("추천 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}