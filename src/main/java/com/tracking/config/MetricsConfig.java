package com.tracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Configuration for application metrics and monitoring.
 * 
 * Defines custom metrics for tracking number generation operations
 * and system performance monitoring.
 */
@Configuration
public class MetricsConfig {
    
    /**
     * Counter for tracking number generation requests.
     * 
     * @param meterRegistry the meter registry
     * @return Counter bean
     */
    @Bean
    public Counter trackingNumberGenerationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("tracking.number.generation.requests")
                .description("Total number of tracking number generation requests")
                .register(meterRegistry);
    }
    
    /**
     * Counter for tracking number generation failures.
     * 
     * @param meterRegistry the meter registry
     * @return Counter bean
     */
    @Bean
    public Counter trackingNumberGenerationFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("tracking.number.generation.failures")
                .description("Total number of tracking number generation failures")
                .register(meterRegistry);
    }
    
    /**
     * Timer for tracking number generation duration.
     * 
     * @param meterRegistry the meter registry
     * @return Timer bean
     */
    @Bean
    public Timer trackingNumberGenerationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("tracking.number.generation.duration")
                .description("Duration of tracking number generation operations")
                .register(meterRegistry);
    }
}
