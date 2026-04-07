package com.telemetry.monitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI monitoringOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Telemetry Monitoring API")
                        .description("Handles the live streaming of classified packets and modifying fields")
                        .version("v1")
                );
    }

}
