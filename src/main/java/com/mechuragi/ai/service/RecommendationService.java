package com.mechuragi.ai.service;

import com.mechuragi.ai.client.MainServiceClient;
import com.mechuragi.ai.dto.external.FoodRecommendationRequest;
import com.mechuragi.ai.dto.external.FoodRecommendationResponse;
import com.mechuragi.ai.dto.external.SaveRecommendationRequest;
import com.mechuragi.ai.dto.external.SaveRecommendationsRequest;
import com.mechuragi.ai.dto.internal.BedrockPromptRequest;
import com.mechuragi.ai.dto.internal.FoodPreferenceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final BedrockService bedrockService;
    private final MainServiceClient mainServiceClient;

    public FoodRecommendationResponse generateAndSaveRecommendation(String authorization, FoodRecommendationRequest request) {
        // 프론트에서 전달받은 사용자 취향 데이터를 사용
        FoodPreferenceDto preference = FoodPreferenceDto.builder()
                .dietStatus(request.getDietStatus())
                .veganOption(request.getVeganOption())
                .spiceLevel(request.getSpiceLevel())
                .foodTypes(request.getFoodTypes())
                .tastes(request.getTastes())
                .dislikedFoods(request.getDislikedFoods())
                .build();

        BedrockPromptRequest promptRequest = BedrockPromptRequest.builder()
                .type(request.getType())
                .preference(preference)
                .weatherConditions(request.getWeatherConditions())
                .timeOfDay(request.getTimeOfDay())
                .ingredients(request.getIngredients())
                .feeling(request.getFeeling())
                .userMessage(request.getUserMessage())
                .build();

        FoodRecommendationResponse response = bedrockService.generateRecommendation(promptRequest);

        saveRecommendationsAsync(authorization, request.getType(), response);

        return response;
    }

    @Async
    public void saveRecommendationsAsync(String authorization, com.mechuragi.ai.type.RecommendationType type,
                                         FoodRecommendationResponse response) {
        try {
            if (response.getRecommendations() == null || response.getRecommendations().isEmpty()) {
                log.warn("저장할 추천 결과가 없습니다");
                return;
            }

            List<SaveRecommendationRequest> saveRequests = response.getRecommendations().stream()
                    .map(rec -> SaveRecommendationRequest.builder()
                            .recommendationType(type)
                            .name(rec.getName())
                            .description(rec.getDescription())
                            .reason(rec.getReason())
                            .ingredients(rec.getIngredients())
                            .cookingTime(rec.getCookingTime())
                            .difficulty(rec.getDifficulty())
                            .build())
                    .collect(Collectors.toList());

            SaveRecommendationsRequest request = SaveRecommendationsRequest.builder()
                    .recommendations(saveRequests)
                    .build();

            mainServiceClient.saveRecommendations(authorization, request)
                    .subscribe(
                            result -> log.info("비동기 저장 완료"),
                            error -> log.error("비동기 저장 실패", error)
                    );

        } catch (Exception e) {
            log.error("추천 결과 비동기 저장 중 오류", e);
        }
    }
}
