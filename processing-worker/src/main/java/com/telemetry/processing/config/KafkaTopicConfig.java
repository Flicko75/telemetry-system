package com.telemetry.processing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic telemetryNormal(){
        return TopicBuilder.name("telemetry.normal")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic telemetryNearCritical(){
        return TopicBuilder.name("telemetry.near-critical")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic telemetryCritical(){
        return TopicBuilder.name("telemetry.critical")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
