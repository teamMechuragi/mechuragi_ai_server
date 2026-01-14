package com.mechuragi.ai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!production")  // 프로덕션 환경에서는 Swagger 비활성화
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // JWT Bearer 토큰 인증 스키마 정의
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer 토큰 (메인 서버로 전달되어 사용자 인증에 사용)");

        return new OpenAPI()
                .info(new Info()
                        .title("메추라기 AI 추천 API")
                        .description("AWS Bedrock Claude 기반 개인화 음식 추천 API")
                        .version("v1.0.3"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
