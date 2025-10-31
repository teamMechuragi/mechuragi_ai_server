package com.mechuragi.ai.controller;

import com.mechuragi.ai.dto.FoodRecommendationRequest;
import com.mechuragi.ai.dto.FoodRecommendationResponse;
import com.mechuragi.ai.service.BedrockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired(required = false)
    private BedrockService bedrockService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "mechuragi-ai-service",
            "version", "1.0.2"
        ));
    }

    @PostMapping("/recommend")
    public ResponseEntity<FoodRecommendationResponse> recommend(@Valid @RequestBody FoodRecommendationRequest request) {
        try {
            log.info("음식 추천 요청: {}", request.getType());

            FoodRecommendationResponse response;
            if (bedrockService != null) {
                log.info("실제 AWS Bedrock 서비스 사용");
                response = bedrockService.generateRecommendation(request);
            } else {
                throw new RuntimeException("사용 가능한 AI 서비스가 없습니다");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("음식 추천 실패", e);
            FoodRecommendationResponse errorResponse = FoodRecommendationResponse.builder()
                .message("추천 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyze(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
            "analysis", "AI 분석 기능 개발 중...!",
            "input", request
        ));
    }
}