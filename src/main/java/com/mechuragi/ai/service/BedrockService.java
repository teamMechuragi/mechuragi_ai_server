package com.mechuragi.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mechuragi.ai.dto.bedrock.BedrockRecommendationResponse;
import com.mechuragi.ai.dto.frontend.FoodRecommendationResponse;
import com.mechuragi.ai.dto.bedrock.BedrockPromptRequest;
import com.mechuragi.ai.type.RecommendationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BedrockService {

    private static final Logger log = LoggerFactory.getLogger(BedrockService.class);

    private final BedrockRuntimeClient bedrockClient;
    private final PromptTemplateService promptTemplateService;
    private final ObjectMapper objectMapper;

    @Value("${aws.bedrock.chat-model}")
    private String chatModel;

    public BedrockService(BedrockRuntimeClient bedrockClient,
                         PromptTemplateService promptTemplateService,
                         ObjectMapper objectMapper) {
        this.bedrockClient = bedrockClient;
        this.promptTemplateService = promptTemplateService;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "foodRecommendations", key = "#request.hashCode() + '_' + T(java.lang.System).currentTimeMillis() / 600000L")
    public FoodRecommendationResponse generateRecommendation(BedrockPromptRequest request) {
        try {
            String prompt = promptTemplateService.generatePrompt(request);

            String requestBody = createClaudeRequest(prompt);
            log.debug("Bedrock 요청 Body: {}", requestBody);

            InvokeModelRequest invokeRequest = InvokeModelRequest.builder()
                    .modelId(chatModel)
                    .body(SdkBytes.fromUtf8String(requestBody))
                    .build();

            long bedrockStart = System.currentTimeMillis();
            InvokeModelResponse response = bedrockClient.invokeModel(invokeRequest);
            log.info("[성능] Bedrock 추천 응답 생성: {}ms", System.currentTimeMillis() - bedrockStart);

            String responseBody = response.body().asUtf8String();

            log.debug("Bedrock 응답 Body: {}", responseBody);

            FoodRecommendationResponse parsed = parseClaudeResponse(responseBody);
            return applyRecommendationType(parsed, request.getType());

        } catch (Exception e) {
            log.error("음식 추천 생성 실패", e);
            throw new RuntimeException("음식 추천 서비스 호출 중 오류 발생", e);
        }
    }

    private String createClaudeRequest(String prompt) throws JsonProcessingException {
        var requestBody = new ClaudeRequest();
        requestBody.anthropic_version = "bedrock-2023-05-31";
        requestBody.max_tokens = 1000;
        requestBody.temperature = 0.9;
        requestBody.messages = new ClaudeRequest.Message[]{
            new ClaudeRequest.Message("user", prompt)
        };

        return objectMapper.writeValueAsString(requestBody);
    }

    private FoodRecommendationResponse parseClaudeResponse(String responseBody) {
        try {
            var claudeResponse = objectMapper.readValue(responseBody, ClaudeResponse.class);
            String content = claudeResponse.content[0].text;

            String jsonContent = extractJsonFromResponse(content);
            log.debug("추출된 JSON: {}", jsonContent);

            // Claude가 recommendationType에 "CUISINE", "일식" 같은 엉뚱한 값을 넣어도
            // 예외를 던지지 않고 null로 처리하도록 설정.
            // (어차피 아래 applyRecommendationType()에서 올바른 값으로 덮어씀)
            ObjectMapper lenientMapper = objectMapper.copy()
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            return lenientMapper.readValue(jsonContent, FoodRecommendationResponse.class);

        } catch (Exception e) {
            log.error("Claude 응답 파싱 실패: {}", responseBody, e);

            return FoodRecommendationResponse.builder().build();
        }
    }

    private FoodRecommendationResponse applyRecommendationType(FoodRecommendationResponse response, RecommendationType type) {
        if (response.getRecommendations() == null) return response;

        List<BedrockRecommendationResponse> fixed = response.getRecommendations().stream()
                .map(r -> BedrockRecommendationResponse.builder()
                        .recommendationType(type)
                        .name(r.getName())
                        .reason(r.getReason())
                        .build())
                .collect(Collectors.toList());

        return FoodRecommendationResponse.builder()
                .recommendations(fixed)
                .model(chatModel)
                .build();
    }

    private String extractJsonFromResponse(String content) {
        int jsonStart = content.indexOf("```json");
        int jsonEnd = content.lastIndexOf("```");

        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return content.substring(jsonStart + 7, jsonEnd).trim();
        }

        int braceStart = content.indexOf("{");
        int braceEnd = content.lastIndexOf("}");

        if (braceStart != -1 && braceEnd != -1 && braceEnd > braceStart) {
            return content.substring(braceStart, braceEnd + 1);
        }

        return content;
    }

    private static class ClaudeRequest {
        public String anthropic_version;
        public int max_tokens;
        public double temperature;
        public Message[] messages;

        static class Message {
            public String role;
            public String content;

            Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }

    private static class ClaudeResponse {
        public String id;
        public String type;
        public String role;
        public Content[] content;
        public String model;
        public String stopReason;
        public String stopSequence;
        public Usage usage;

        static class Content {
            public String type;
            public String text;
        }

        static class Usage {
            public int inputTokens;
            public int outputTokens;
        }
    }
}