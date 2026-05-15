package com.geekup.flashsale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point for the Flashsale service.
 * Enables Spring Boot auto-configuration and scheduled background jobs.
 */
@SpringBootApplication
@EnableScheduling
public class FlashsaleApplication {
    /**
     * Starts the Spring Boot application context.
     *
     * @param args runtime arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FlashsaleApplication.class, args);
    }
}
