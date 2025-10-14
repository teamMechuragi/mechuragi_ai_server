package com.mechuragi.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mechuragi.ai.dto.FoodRecommendationRequest;
import com.mechuragi.ai.dto.FoodRecommendationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

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

    @Cacheable(value = "foodRecommendations", key = "#request.hashCode()")
    public FoodRecommendationResponse generateRecommendation(FoodRecommendationRequest request) {
        try {
            String prompt = promptTemplateService.generatePrompt(request);

            String requestBody = createClaudeRequest(prompt);
            log.debug("Bedrock 요청 Body: {}", requestBody);

            InvokeModelRequest invokeRequest = InvokeModelRequest.builder()
                    .modelId(chatModel)
                    .body(SdkBytes.fromUtf8String(requestBody))
                    .build();

            InvokeModelResponse response = bedrockClient.invokeModel(invokeRequest);
            String responseBody = response.body().asUtf8String();

            log.debug("Bedrock 응답 Body: {}", responseBody);

            return parseClaudeResponse(responseBody);

        } catch (Exception e) {
            log.error("음식 추천 생성 실패", e);
            throw new RuntimeException("음식 추천 서비스 호출 중 오류 발생", e);
        }
    }

    private String createClaudeRequest(String prompt) throws JsonProcessingException {
        var requestBody = new ClaudeRequest();
        requestBody.anthropicVersion = "bedrock-2023-05-31";
        requestBody.maxTokens = 4000;
        requestBody.temperature = 0.7;
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

            return objectMapper.readValue(jsonContent, FoodRecommendationResponse.class);

        } catch (Exception e) {
            log.error("Claude 응답 파싱 실패: {}", responseBody, e);

            FoodRecommendationResponse errorResponse = new FoodRecommendationResponse();
            errorResponse.setMessage("AI 응답을 처리하는 중 오류가 발생했습니다.");
            return errorResponse;
        }
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
        public String anthropicVersion;
        public int maxTokens;
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