package com.fantasyunlimited.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi contentListings() {
        return GroupedOpenApi.builder()
                .group("Content")
                .pathsToMatch("/api/content/**")
                .packagesToScan("com.fantasyunlimited.rest")
                .build();
    }

    @Bean
    public GroupedOpenApi userOperations() {
        return GroupedOpenApi.builder()
                .group("User Operations")
                .pathsToMatch("/api/user/**")
                .packagesToScan("com.fantasyunlimited.rest")
                .build();
    }

    @Bean
    public GroupedOpenApi gameOperations() {
        return GroupedOpenApi.builder()
                .group("Game Operations")
                .pathsToMatch("/api/game/**")
                .packagesToScan("com.fantasyunlimited.rest")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("FantasyUnlimited API")
                        .description("Fantasy Unlimited - REST API Description")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")
                                              .url("https://fantasyunlimited.com")
                        )
                );
    }
}
