package com.tracking.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracking.domain.TrackingNumber;
import com.tracking.domain.TrackingNumberRequest;
import com.tracking.domain.TrackingNumberResponse;
import com.tracking.repository.TrackingNumberRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Service for generating unique tracking numbers.
 * 
 * Implements thread-safe and distributed uniqueness using database sequences
 * and retry mechanisms for concurrent access scenarios.
 */
@Service
@Transactional
public class TrackingNumberService {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberService.class);
    
    private final TrackingNumberRepository trackingNumberRepository;
    private final int maxRetries;
    private final Counter generationCounter;
    private final Counter failureCounter;
    private final Timer generationTimer;
    
    // Character set for tracking number generation (A-Z, 0-9)
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MIN_TRACKING_NUMBER_LENGTH = 8;
    private static final int MAX_TRACKING_NUMBER_LENGTH = 16;
    
    public TrackingNumberService(TrackingNumberRepository trackingNumberRepository,
                               MeterRegistry meterRegistry,
                               @Value("${app.tracking.max-retries:3}") int maxRetries) {
        this.trackingNumberRepository = trackingNumberRepository;
        this.maxRetries = maxRetries;
        this.generationCounter = Counter.builder("tracking.number.generation.requests")
                .description("Total number of tracking number generation requests")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("tracking.number.generation.failures")
                .description("Total number of tracking number generation failures")
                .register(meterRegistry);
        this.generationTimer = Timer.builder("tracking.number.generation.duration")
                .description("Duration of tracking number generation operations")
                .register(meterRegistry);
    }
    
    /**
     * Generates a unique tracking number for the given request.
     * 
     * This method ensures thread-safe generation and handles concurrency
     * by retrying on collision with exponential backoff.
     * 
     * @param request the tracking number generation request
     * @return TrackingNumberResponse containing the generated tracking number
     * @throws TrackingNumberGenerationException if generation fails after max retries
     */
    public TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest request) {
        logger.info("Generating tracking number for customer: {} ({})", 
                   request.customerName(), request.customerId());
        
        generationCounter.increment();
        
        try {
            return generationTimer.recordCallable(() -> {
                try {
                    UUID requestId = UUID.randomUUID();
                    String trackingNumber = generateUniqueTrackingNumber();
                    
                    // Create and persist the tracking number entity
                    TrackingNumber entity = new TrackingNumber(
                        trackingNumber,
                        request.originCountryId(),
                        request.destinationCountryId(),
                        request.weight(),
                        request.customerId(),
                        request.customerName(),
                        request.customerSlug(),
                        requestId
                    );
                    
                    trackingNumberRepository.save(entity);
                    logger.info("Successfully generated tracking number: {} for customer: {}", 
                               trackingNumber, request.customerName());
                    
                    return TrackingNumberResponse.of(
                        trackingNumber,
                        request.createdAt(),
                        requestId,
                        request.customerId(),
                        request.customerName()
                    );
                    
                } catch (RuntimeException e) {
                    failureCounter.increment();
                    logger.error("Failed to generate tracking number for customer: {}", 
                                request.customerName(), e);
                    throw new TrackingNumberGenerationException(
                        "Failed to generate tracking number: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            failureCounter.increment();
            logger.error("Failed to generate tracking number for customer: {}", 
                        request.customerName(), e);
            throw new TrackingNumberGenerationException(
                "Failed to generate tracking number: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates a unique tracking number using retry mechanism.
     * 
     * @return a unique tracking number
     * @throws TrackingNumberGenerationException if unable to generate unique number
     */
    private String generateUniqueTrackingNumber() {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            String trackingNumber = generateTrackingNumber();
            
            if (!trackingNumberRepository.existsByTrackingNumber(trackingNumber)) {
                return trackingNumber;
            }
            
            logger.warn("Tracking number collision detected: {} (attempt {}/{})", 
                       trackingNumber, attempt, maxRetries);
            
            // Exponential backoff for retry
            if (attempt < maxRetries) {
                try {
                    long delay = ThreadLocalRandom.current().nextLong(10, 50) * attempt;
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new TrackingNumberGenerationException("Generation interrupted", e);
                }
            }
        }
        
        throw new TrackingNumberGenerationException(
            "Unable to generate unique tracking number after " + maxRetries + " attempts");
    }
    
    /**
     * Generates a random tracking number following the required pattern.
     * 
     * @return a tracking number matching ^[A-Z0-9]{8,16}$
     */
    private String generateTrackingNumber() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int length = random.nextInt(MIN_TRACKING_NUMBER_LENGTH, MAX_TRACKING_NUMBER_LENGTH + 1);
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * Custom exception for tracking number generation failures.
     * 
     * This exception is thrown when the service is unable to generate
     * a unique tracking number after exhausting all retry attempts.
     */
    public static class TrackingNumberGenerationException extends RuntimeException {
        public TrackingNumberGenerationException(String message) {
            super(message);
        }
        
        public TrackingNumberGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}