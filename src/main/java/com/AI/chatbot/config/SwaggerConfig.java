package com.AI.chatbot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "API Documentation",
                description = "API Documentation for the project",
                version = "v1"))
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi v1OpenApi() {
        String[] paths = {"/v1/**"};

        return GroupedOpenApi.builder()
                .group("v1 API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/api/chatbot/**"};

        return GroupedOpenApi.builder()
                .group("ChatBot API")
                .pathsToMatch(paths)
                .build();
    }
}