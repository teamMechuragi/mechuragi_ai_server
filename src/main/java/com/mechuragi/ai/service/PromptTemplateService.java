package com.mechuragi.ai.service;

import com.mechuragi.ai.dto.bedrock.BedrockPromptRequest;
import com.mechuragi.ai.dto.frontend.FoodPreferenceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    @Value("${aws.bedrock.chat-model}")
    private String chatModel;

    public String generatePrompt(BedrockPromptRequest request) {
        return switch (request.getType()) {
            case WEATHER -> generateWeatherBasedPrompt(request);
            case TIME -> generateTimeBasedPrompt(request);
            case INGREDIENTS -> generateIngredientsBasedPrompt(request);
            case FEELING -> generateFeelingBasedPrompt(request);
            case CONVERSATION -> generateConversationBasedPrompt(request);
        };
    }

    private String generateWeatherBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("음식 추천 전문가입니다. 현재 날씨에 어울리는 음식 3가지를 추천해주세요.\n\n");
        prompt.append("날씨: ").append(String.join(", ", request.getContext())).append("\n\n");
        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);
        return prompt.toString();
    }

    private String generateTimeBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("음식 추천 전문가입니다. 현재 시간대에 어울리는 음식 3가지를 추천해주세요.\n\n");
        prompt.append("시간대: ").append(String.join(", ", request.getContext())).append("\n\n");
        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);
        return prompt.toString();
    }

    private String generateIngredientsBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("음식 추천 전문가입니다. 보유한 재료로 만들 수 있는 음식 3가지를 추천해주세요.\n\n");
        prompt.append("보유 재료: ").append(String.join(", ", request.getContext())).append("\n\n");
        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);
        return prompt.toString();
    }

    private String generateFeelingBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("음식 추천 전문가입니다. 현재 기분에 어울리는 음식 3가지를 추천해주세요.\n\n");
        prompt.append("기분: ").append(String.join(", ", request.getContext())).append("\n\n");
        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);
        return prompt.toString();
    }

    private String generateConversationBasedPrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("음식 추천 전문가입니다. 사용자 요청에 맞는 음식 3가지를 추천해주세요.\n\n");
        prompt.append("요청: ").append(String.join(", ", request.getContext())).append("\n\n");
        appendUserPreferences(prompt, request.getPreference());
        appendResponseFormat(prompt);
        return prompt.toString();
    }

    private void appendUserPreferences(StringBuilder prompt, FoodPreferenceRequest pref) {
        prompt.append("## 취향 조건 (반드시 준수)\n");
        prompt.append("- 다이어트: ").append(pref.getDietStatus()).append("\n");
        prompt.append("- 비건: ").append(pref.getVeganOption()).append("\n");
        prompt.append("- 매운맛: ").append(pref.getSpiceLevel()).append("\n");

        if (pref.getFoodTypes() != null && !pref.getFoodTypes().isEmpty()) {
            prompt.append("- 허용 음식 종류(이 범위 내에서만 추천): ").append(String.join(", ", pref.getFoodTypes())).append("\n");
        }

        if (pref.getTastes() != null && !pref.getTastes().isEmpty()) {
            prompt.append("- 선호 맛: ").append(String.join(", ", pref.getTastes())).append("\n");
        }

        if (pref.getDislikedFoods() != null && !pref.getDislikedFoods().isEmpty()) {
            prompt.append("- 절대 추천 금지: ").append(String.join(", ", pref.getDislikedFoods())).append("\n");
        }

        prompt.append("- 3가지 추천은 서로 다른 계열의 음식으로 구성\n");
        prompt.append("\n");
    }

    private void appendResponseFormat(StringBuilder prompt) {
        prompt.append("JSON만 반환. 다른 텍스트 없음.\n\n");
        prompt.append("{\"message\":\"추천 인사말\",\"recommendations\":[{\"recommendationType\":\"TYPE\",\"name\":\"음식명\",\"description\":\"한 줄 설명\",\"reason\":\"추천 이유\"}],\"model\":\"").append(chatModel).append("\"}\n");
    }
}