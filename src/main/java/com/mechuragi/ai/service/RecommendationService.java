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

    private final FoodPreferenceService foodPreferenceService;
    private final BedrockService bedrockService;
    private final MainServiceClient mainServiceClient;

    public FoodRecommendationResponse generateAndSaveRecommendation(Long memberId, FoodRecommendationRequest request) {
        FoodPreferenceDto preference = foodPreferenceService.getActivePreference(memberId);

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

        saveRecommendationsAsync(memberId, request.getType(), response);

        return response;
    }

    @Async
    public void saveRecommendationsAsync(Long memberId, com.mechuragi.ai.type.RecommendationType type,
                                         FoodRecommendationResponse response) {
        try {
            if (response.getRecommendations() == null || response.getRecommendations().isEmpty()) {
                log.warn("저장할 추천 결과가 없습니다 - 회원: {}", memberId);
                return;
            }

            List<SaveRecommendationRequest> saveRequests = response.getRecommendations().stream()
                    .map(rec -> SaveRecommendationRequest.builder()
                            .memberId(memberId)
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
                    .memberId(memberId)
                    .recommendations(saveRequests)
                    .build();

            mainServiceClient.saveRecommendations(request)
                    .subscribe(
                            result -> log.info("비동기 저장 완료 - 회원: {}", memberId),
                            error -> log.error("비동기 저장 실패 - 회원: {}", memberId, error)
                    );

        } catch (Exception e) {
            log.error("추천 결과 비동기 저장 중 오류 - 회원: {}", memberId, e);
        }
    }
}
