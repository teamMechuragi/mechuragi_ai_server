package com.mechuragi.ai.controller;

import com.mechuragi.ai.dto.external.FoodRecommendationRequest;
import com.mechuragi.ai.dto.external.FoodRecommendationResponse;
import com.mechuragi.ai.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "mechuragi-ai-service",
            "version", "1.0.2"
        ));
    }

    @PostMapping
    public ResponseEntity<FoodRecommendationResponse> recommend(
            @RequestHeader(value = "X-Member-Id", required = true) Long memberId,
            @Valid @RequestBody FoodRecommendationRequest request) {
        try {
            log.info("음식 추천 요청 - 회원: {}, 타입: {}", memberId, request.getType());

            FoodRecommendationResponse response = recommendationService.generateAndSaveRecommendation(memberId, request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("음식 추천 실패 - 회원: {}", memberId, e);
            FoodRecommendationResponse errorResponse = FoodRecommendationResponse.builder()
                .message("추천 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}