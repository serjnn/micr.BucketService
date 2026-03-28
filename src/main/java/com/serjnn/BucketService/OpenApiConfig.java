package com.serjnn.BucketService;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Bucket Service}")
    private String applicationName;

    @Value("${OPENAPI_TITLE:Bucket Service API}")
    private String title;

    @Value("${OPENAPI_VERSION:1.0}")
    private String version;

    @Value("${OPENAPI_DESCRIPTION:Documentation Bucket Service API v1.0}")
    private String description;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description));
    }
}
