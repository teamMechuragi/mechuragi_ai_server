package com.mechuragi.ai.service;

import com.mechuragi.ai.dto.bedrock.BedrockPromptRequest;
import com.mechuragi.ai.dto.frontend.FoodPreferenceRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromptTemplateService {

    public String generatePrompt(BedrockPromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        // 1. 페르소나 설정
        prompt.append("너는 사용자의 취향과 제약 사항을 완벽히 준수하는 전문 푸드 큐레이터야.\n");
        prompt.append("제시된 상황과 사용자의 선호도를 조합해 최적의 메뉴 3가지를 추천해줘.\n\n");

        // 2. 현재 상황 (필수)
        prompt.append("## 1. 현재 상황\n");
        prompt.append(getContextDescription(request)).append("\n\n");

        // 3. 사용자 취향 (필수 & 선택 혼합)
        appendUserPreferences(prompt, request.getPreference());

        // 4. 출력 형식 (JSON 고정)
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private String getContextDescription(BedrockPromptRequest request) {
        String context = String.join(", ", request.getContext());
        return switch (request.getType()) {
            case WEATHER -> "날씨: " + context;
            case TIME -> "시간대: " + context;
            case INGREDIENTS -> "보유 재료: " + context;
            case FEELING -> "기분/상황: " + context;
            case CONVERSATION -> "요청: " + context;
        };
    }

    private void appendUserPreferences(StringBuilder prompt, FoodPreferenceRequest pref) {
        prompt.append("## 2. 사용자 취향 (반드시 준수)\n");

        // [필수 항목]
        prompt.append("- 식사 인원: ").append(pref.getNumberOfDiners()).append("명\n");
        prompt.append("- 다이어트 상태: ").append(dietStatusToKorean(pref.getDietStatus())).append("\n");
        prompt.append("- 비건 단계: ").append(veganOptionToKorean(pref.getVeganOption())).append("\n");
        prompt.append("- 선호하는 매운맛 레벨: ").append(spiceLevelToKorean(pref.getSpiceLevel())).append("\n");
        prompt.append("- 선호 카테고리: ").append(String.join(", ", pref.getFoodTypes())).append("\n");
        prompt.append("- 선호하는 맛: ").append(String.join(", ", pref.getTastes())).append("\n");

        // [선택 항목] 알레르기 및 기피 음식
        List<String> exclusions = new ArrayList<>();
        if (pref.getAvoidedFoods() != null && !pref.getAvoidedFoods().isEmpty()) {
            exclusions.addAll(pref.getAvoidedFoods());
        }
        if (pref.getAllergies() != null && !pref.getAllergies().isEmpty()) {
            exclusions.addAll(pref.getAllergies());
        }
        if (!exclusions.isEmpty()) {
            prompt.append("- **[절대 추천 금지]** 제외 식재료: ").append(String.join(", ", exclusions)).append("\n");
        }

        prompt.append("\n");
    }

    private void appendResponseFormat(StringBuilder prompt) {
        prompt.append("## 3. 응답 규칙\n");
        prompt.append("- 반드시 JSON 형식으로만 응답할 것.\n");
        prompt.append("- 'reason'은 해당 메뉴가 왜 상황과 취향에 적합한지 1문장으로 친절하게 설명할 것.\n\n");
        prompt.append("{\n");
        prompt.append("  \"recommendations\": [\n");
        prompt.append("    { \"name\": \"음식명\", \"reason\": \"이유\" }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
    }

    private String spiceLevelToKorean(String spiceLevel) {
        if (spiceLevel == null) return "순한맛";
        return switch (spiceLevel) {
            case "VERY_MILD" -> "맵찔이";
            case "MILD" -> "순한맛";
            case "MEDIUM" -> "신라면";
            case "HOT" -> "불닭";
            case "EXTREME" -> "핵불닭";
            default -> "순한맛";
        };
    }

    private String dietStatusToKorean(String status) {
        if (status == null) return "해당 없음";
        return switch (status) {
            case "WEIGHT_LOSS" -> "다이어트 중";
            case "BULKING" -> "근성장";
            case "MAINTENANCE" -> "유지어터";
            default -> "해당 없음";
        };
    }

    private String veganOptionToKorean(String veganOption) {
        if (veganOption == null) return "해당 없음 (일반식)";
        return switch (veganOption) {
            case "VEGAN" -> "비건 (완전 채식)";
            case "VEGETARIAN" -> "베지테리언 (유제품/달걀 허용)";
            case "PESCATARIAN" -> "페스코 (생선까지 허용)";
            case "FLEXITARIAN" -> "플렉시테리언 (간헐적 채식)";
            default -> "해당 없음 (일반식)";
        };
    }
}
