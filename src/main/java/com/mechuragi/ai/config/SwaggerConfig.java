package com.mechuragi.ai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!production")  // 프로덕션 환경에서는 Swagger 비활성화
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // X-Member-Id 헤더 파라미터 정의
        Parameter memberIdHeader = new Parameter()
                .in("header")
                .name("X-Member-Id")
                .description("회원 ID (사용자 식별)")
                .required(true)
                .schema(new io.swagger.v3.oas.models.media.Schema<Long>().type("integer").format("int64"));

        return new OpenAPI()
                .info(new Info()
                        .title("메추라기 AI 추천 API")
                        .description("AWS Bedrock Claude 기반 개인화 음식 추천 API")
                        .version("v1.0.2"))
                .components(new Components()
                        .addParameters("X-Member-Id", memberIdHeader));
    }
}
