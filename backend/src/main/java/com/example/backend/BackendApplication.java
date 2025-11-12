package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point.
 *
 * PUBLIC_INTERFACE
 */
@SpringBootApplication
public class BackendApplication {

    // PUBLIC_INTERFACE
    /**
     * Application main entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
