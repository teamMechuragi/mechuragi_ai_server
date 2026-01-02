package com.mechuragi.ai.service;

import com.mechuragi.ai.dto.internal.BedrockPromptRequest;
import com.mechuragi.ai.dto.internal.FoodPreferenceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    @Value("${aws.bedrock.chat-model}")
    private String chatModel;

    public String generatePrompt(BedrockPromptRequest request) {
        return switch (request.getType()) {
            case WEATHER -> generateWeatherBasedPrompt(request);
            case TIME_BASED -> generateTimeBasedPrompt(request);
            case INGREDIENTS -> generateIngredientsBasedPrompt(request);
            case FEELING -> generateFeelingBasedPrompt(request);
            case CONVERSATION -> generateConversationBasedPrompt(request);
        };
    }

    private String generateWeatherBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 한국 음식 전문가입니다. 현재 날씨 상황과 사용자의 음식 취향을 고려하여 적절한 음식 3가지를 추천해주세요.\n\n");

        prompt.append("## 현재 날씨 상황\n");
        prompt.append("날씨: ").append(String.join(", ", request.getWeatherConditions())).append("\n\n");

        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private String generateTimeBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 한국 음식 전문가입니다. 현재 시간대와 사용자의 음식 취향을 고려하여 적절한 음식 3가지를 추천해주세요.\n\n");

        prompt.append("## 현재 시간대\n");
        prompt.append("시간: ").append(request.getTimeOfDay()).append("\n\n");

        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private String generateIngredientsBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 한국 음식 전문가입니다. 사용자가 가진 재료와 음식 취향을 고려하여 적절한 음식 3가지를 추천해주세요.\n\n");

        prompt.append("## 보유 재료\n");
        prompt.append("재료: ").append(String.join(", ", request.getIngredients())).append("\n\n");

        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private String generateFeelingBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 한국 음식 전문가입니다. 사용자의 현재 기분과 음식 취향을 고려하여 적절한 음식 3가지를 추천해주세요.\n\n");

        prompt.append("## 현재 기분\n");
        prompt.append("기분: ").append(request.getFeeling()).append("\n\n");

        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private String generateConversationBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 친근한 한국 음식 전문가입니다. 사용자의 요청과 음식 취향을 고려하여 적절한 음식 3가지를 추천해주세요.\n\n");

        prompt.append("## 사용자 요청\n");
        prompt.append(request.getUserMessage()).append("\n\n");

        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private void appendUserPreferences(StringBuilder prompt, FoodPreferenceDto pref) {
        prompt.append("## 사용자 음식 취향\n");
        prompt.append("- 다이어트 상태: ").append(pref.getDietStatus()).append("\n");
        prompt.append("- 비건 옵션: ").append(pref.getVeganOption()).append("\n");
        prompt.append("- 매운 정도: ").append(pref.getSpiceLevel()).append("\n");

        if (pref.getFoodTypes() != null && !pref.getFoodTypes().isEmpty()) {
            prompt.append("- 선호 음식 종류: ").append(String.join(", ", pref.getFoodTypes())).append("\n");
        }

        if (pref.getTastes() != null && !pref.getTastes().isEmpty()) {
            prompt.append("- 선호 맛: ").append(String.join(", ", pref.getTastes())).append("\n");
        }

        if (pref.getDislikedFoods() != null && !pref.getDislikedFoods().isEmpty()) {
            prompt.append("- 기피 음식: ").append(String.join(", ", pref.getDislikedFoods())).append("\n");
        }

        prompt.append("\n");
    }

    private void appendResponseFormat(StringBuilder prompt) {
        prompt.append("## 응답 형식\n");
        prompt.append("다음 JSON 형식으로만 응답해주세요. 다른 텍스트는 포함하지 마세요.\n\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"message\": \"추천 인사말\",\n");
        prompt.append("  \"recommendations\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"recommendationType\": \"WEATHER 또는 TIME_BASED 또는 INGREDIENTS 또는 FEELING 또는 CONVERSATION\",\n");
        prompt.append("      \"name\": \"음식 이름\",\n");
        prompt.append("      \"description\": \"음식 설명\",\n");
        prompt.append("      \"reason\": \"추천 이유\",\n");
        prompt.append("      \"ingredients\": \"재료들을 콤마로 구분\",\n");
        prompt.append("      \"cookingTime\": \"조리 시간\",\n");
        prompt.append("      \"difficulty\": \"난이도\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"model\": \"").append(chatModel).append("\"\n");
        prompt.append("}\n");
        prompt.append("```\n");
    }
}