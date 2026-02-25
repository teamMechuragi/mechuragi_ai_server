package com.mechuragi.ai.controller;

import com.mechuragi.ai.dto.bedrock.BedrockRecommendationResponse;
import com.mechuragi.ai.dto.frontend.FoodRecommendationResponse;
import com.mechuragi.ai.type.RecommendationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 부하 테스트용 Mock 엔드포인트
 * Bedrock 호출 없이 고정 지연(Thread.sleep)으로 AI 처리 시뮬레이션
 */
@RestController
@RequestMapping("/recommend/mock")
@Slf4j
public class MockRecommendController {

    @Value("${mock.bedrock-delay-ms:3000}")
    private long bedrockDelayMs;

    @Value("${mock.network-delay-ms:200}")
    private long networkDelayMs;

    @PostMapping("/food")
    public ResponseEntity<FoodRecommendationResponse> mockRecommend(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Object requestBody) throws InterruptedException {

        log.info("[Mock] 추천 요청 수신 - Bedrock {}ms + 네트워크 {}ms 지연 시작", bedrockDelayMs, networkDelayMs);
        long start = System.currentTimeMillis();

        Thread.sleep(bedrockDelayMs + networkDelayMs);

        log.info("[Mock] 지연 완료: {}ms", System.currentTimeMillis() - start);

        FoodRecommendationResponse response = FoodRecommendationResponse.builder()
                .recommendations(List.of(
                        BedrockRecommendationResponse.builder()
                                .recommendationType(RecommendationType.WEATHER)
                                .name("비빔밥")
                                .reason("Mock 응답")
                                .build()
                ))
                .model("mock")
                .build();

        return ResponseEntity.ok(response);
    }
}
