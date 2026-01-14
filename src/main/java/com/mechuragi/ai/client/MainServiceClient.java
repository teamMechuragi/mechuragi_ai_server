package com.mechuragi.ai.client;

import com.mechuragi.ai.dto.main.SaveRecommendationsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainServiceClient {

    private final WebClient mainServiceWebClient;

    public Mono<Void> saveRecommendations(String authorization, SaveRecommendationsRequest request) {
        return mainServiceWebClient.post()
                .uri("/api/ai/recommended-foods/save")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("추천 결과 메인 서버 저장 성공 - 개수: {}",
                        request.getRecommendations().size()))
                .doOnError(e -> log.error("추천 결과 메인 서버 저장 실패", e));
    }
}
