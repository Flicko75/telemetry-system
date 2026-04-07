package com.telemetry.ingestion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ingestionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telemetry Ingestion API")
                        .description("Handles incoming telemetry packets from the device")
                        .version("v1")
                );
    }

}
