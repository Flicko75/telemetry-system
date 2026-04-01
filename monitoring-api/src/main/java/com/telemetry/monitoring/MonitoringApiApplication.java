package com.telemetry.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.telemetry.monitoring.repos")
public class MonitoringApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringApiApplication.class, args);
    }

}
