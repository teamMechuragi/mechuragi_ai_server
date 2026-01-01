package com.mechuragi.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${main-service.url}")
    private String mainServiceUrl;

    @Bean
    public WebClient mainServiceWebClient() {
        return WebClient.builder()
                .baseUrl(mainServiceUrl)
                .build();
    }
}
