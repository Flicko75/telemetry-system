package com.telemetry.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.telemetry.processing.repos")
public class ProcessingWorkerApplication {

    public static void main(String[] args){
        SpringApplication.run(ProcessingWorkerApplication.class, args);
    }

}
