package com.mechuragi.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mechuragi.ai.dto.FoodRecommendationRequest;
import com.mechuragi.ai.dto.FoodRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BedrockService {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.bedrock.chat-model}")
    private String chatModel;

    private final ObjectMapper objectMapper;
    private final PromptTemplateService promptTemplateService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "food_recommendation:";
    private static final Duration CACHE_DURATION = Duration.ofHours(24);

    public FoodRecommendationResponse generateRecommendation(FoodRecommendationRequest request) {
        String cacheKey = generateCacheKey(request);

        FoodRecommendationResponse cached = (FoodRecommendationResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("캐시에서 추천 결과 반환: {}", cacheKey);
            return cached;
        }

        try {
            BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                    .region(Region.of(awsRegion))
                    .build();

            String prompt = promptTemplateService.generatePrompt(request);

            Map<String, Object> requestBody = Map.of(
                "anthropic_version", "bedrock-2023-05-31",
                "max_tokens", 3000,
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", prompt
                    )
                }
            );

            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            log.info("Bedrock request: {}", jsonRequest);

            InvokeModelRequest invokeRequest = InvokeModelRequest.builder()
                    .modelId(chatModel)
                    .body(SdkBytes.fromUtf8String(jsonRequest))
                    .build();

            InvokeModelResponse response = client.invokeModel(invokeRequest);
            String responseBody = response.body().asUtf8String();
            log.info("Bedrock response: {}", responseBody);

            FoodRecommendationResponse result = parseResponse(responseBody);

            redisTemplate.opsForValue().set(cacheKey, result, CACHE_DURATION);
            log.info("추천 결과 캐시 저장: {}", cacheKey);

            return result;

        } catch (Exception e) {
            log.error("Bedrock API 호출 실패", e);
            throw new RuntimeException("AI 추천 생성 중 오류가 발생했습니다", e);
        }
    }

    private String generateCacheKey(FoodRecommendationRequest request) {
        try {
            String requestStr = objectMapper.writeValueAsString(request);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(requestStr.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return CACHE_PREFIX + sb.toString();
        } catch (Exception e) {
            return CACHE_PREFIX + System.currentTimeMillis();
        }
    }

    private FoodRecommendationResponse parseResponse(String responseBody) {
        try {
            log.info("원본 응답: {}", responseBody);

            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) responseMap.get("content");
            String content = contentList.get(0).get("text").toString();

            log.info("추출된 컨텐츠: {}", content);

            // Claude가 json 블록으로 감싸서 보내주는 경우
            String jsonContent = content;
            if (content.contains("```json")) {
                int startIndex = content.indexOf("```json") + 7;
                int endIndex = content.lastIndexOf("```");
                if (startIndex > 6 && endIndex > startIndex) {
                    jsonContent = content.substring(startIndex, endIndex).trim();
                    log.info("JSON 블록에서 추출한 내용: {}", jsonContent);
                }
            }

            log.info("파싱할 JSON: {}", jsonContent);

            return objectMapper.readValue(jsonContent, FoodRecommendationResponse.class);
        } catch (Exception e) {
            log.error("응답 파싱 실패", e);
            FoodRecommendationResponse errorResponse = new FoodRecommendationResponse();
            errorResponse.setMessage("응답 처리 중 오류가 발생했습니다");
            errorResponse.setModel(chatModel);
            return errorResponse;
        }
    }
}