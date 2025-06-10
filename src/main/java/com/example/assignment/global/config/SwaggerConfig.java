package com.example.assignment.global.config;

import com.example.assignment.global.customizer.ApiErrorResponseCustomizer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ApiErrorResponseCustomizer apiErrorResponseCustomizer;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("바로인턴13기 백엔드 Java 과제 API Document")
                .version("v0.0.1")
                .description("바로인턴13기 백엔드 Java 과제에 대한 API 명세서입니다.");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("assignment API")
                .addOperationCustomizer(apiErrorResponseCustomizer)
                .build();
    }
}
