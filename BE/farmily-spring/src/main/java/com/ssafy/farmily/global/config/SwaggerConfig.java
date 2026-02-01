package com.ssafy.farmily.global.config;

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
        // 인증 스키마 이름 정의
        String jwtSchemeName = "JWT Authorization";
        String deviceKeySchemeName = "Device-Key";
        
        // JWT 보안 설정
        SecurityScheme jwtScheme = new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
            
        // API Key (기기용) 보안 설정
        SecurityScheme deviceKeyScheme = new SecurityScheme()
            .name("X-Device-Key") // 헤더 이름
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER);

        Components components = new Components()
            .addSecuritySchemes(jwtSchemeName, jwtScheme)
            .addSecuritySchemes(deviceKeySchemeName, deviceKeyScheme);

        // 기본 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList(jwtSchemeName)
            .addList(deviceKeySchemeName);

        return new OpenAPI()
            .info(new Info()
                .title("Farmily API 문서")
                .version("v1.0.0")
                .description("Farmily 프로젝트의 REST API 문서입니다."))
            .addSecurityItem(securityRequirement)
            .components(components);
    }
}
