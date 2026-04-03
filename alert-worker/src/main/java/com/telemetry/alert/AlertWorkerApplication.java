package com.telemetry.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.telemetry.alert.repos")
@SpringBootApplication
public class AlertWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertWorkerApplication.class, args);
    }

}
