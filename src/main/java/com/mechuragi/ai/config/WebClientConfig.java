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

    // 로컬 테스트용 - 프론트엔드(3000 포트)에서 오는 요청을 허용하는 CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로 허용
                .allowedOrigins("http://localhost:3000") // 프론트엔드 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 메서드
                .allowCredentials(true); // 쿠키/인증정보 허용 여부
    }
}