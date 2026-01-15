package com.mechuragi.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebClientConfig implements WebMvcConfigurer {

    @Value("${main-service.url}")
    private String mainServiceUrl;

    // 환경별로 다른 메인 서비스 URL을 주입받아 WebClient Bean 등록
    @Bean
    public WebClient mainServiceWebClient() {
        return WebClient.builder()
                .baseUrl(mainServiceUrl)
                .build();
    }

    // CORS 설정 - 로컬 및 프로덕션 환경 허용
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로 허용
                .allowedOriginPatterns(
                        "http://localhost:3000",
                        "https://mechuragi.kro.kr",
                        "https://*.mechuragi.kro.kr"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키/인증정보 허용 여부
    }
}