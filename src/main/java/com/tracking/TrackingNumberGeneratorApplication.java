package com.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Tracking Number Generator API.
 * 
 * This Spring Boot application provides a RESTful API for generating
 * unique parcel tracking numbers with support for concurrent access
 * and horizontal scaling.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class TrackingNumberGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackingNumberGeneratorApplication.class, args);
    }
}
