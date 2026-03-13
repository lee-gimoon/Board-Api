package com.board.api.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";

        // 1. 모든 API 요청에 "jwtAuth"라는 보안 규칙을 적용하겠다고 선언
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 2. "jwtAuth"가 구체적으로 어떤 방식(Bearer JWT)인지 정의
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")             // Bearer 리터럴 사용
                        .bearerFormat("JWT"));        // 포맷은 JWT

        return new OpenAPI()
                .info(new Info()
                        .title("Modular Board API")
                        .description("게시판 시스템 API 명세서")
                        .version("v0.0.1"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}