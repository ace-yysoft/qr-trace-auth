package com.qrtrace.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.qrtrace.api.repository")
public class QrTraceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(QrTraceAuthApplication.class, args);
    }
} 