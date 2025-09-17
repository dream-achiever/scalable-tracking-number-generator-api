package com.tracking.controller;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tracking.domain.TrackingNumberRequest;
import com.tracking.domain.TrackingNumberResponse;
import com.tracking.service.TrackingNumberService;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * REST controller for tracking number generation.
 * 
 * Provides the /next-tracking-number endpoint with proper validation,
 * error handling, and observability features.
 */
@RestController
@RequestMapping("/next-tracking-number")
@Validated
public class TrackingNumberController {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberController.class);
    
    private final TrackingNumberService trackingNumberService;
    
    public TrackingNumberController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }
    
    /**
     * Generates a unique tracking number.
     * 
     * @param originCountryId the origin country ID
     * @param destinationCountryId the destination country ID
     * @param weight the weight
     * @param createdAt the created at timestamp
     * @param customerId the customer ID
     * @param customerName the customer name
     * @param customerSlug the customer slug
     * @return ResponseEntity containing the generated tracking number
     */
    @GetMapping
    public ResponseEntity<TrackingNumberResponse> generateTrackingNumber(
            @RequestParam("origin_country_id") @Pattern(regexp = "^[A-Z]{2}$", message = "Origin country ID must be a valid ISO 3166-1 alpha-2 code") String originCountryId,
            @RequestParam("destination_country_id") @Pattern(regexp = "^[A-Z]{2}$", message = "Destination country ID must be a valid ISO 3166-1 alpha-2 code") String destinationCountryId,
            @RequestParam("weight") @DecimalMin(value = "0.001", message = "Weight must be at least 0.001 kg") @DecimalMax(value = "999.999", message = "Weight must not exceed 999.999 kg") Double weight,
            @RequestParam("created_at") String createdAt,
            @RequestParam("customer_id") String customerId,
            @RequestParam("customer_name") @NotBlank(message = "Customer name is required") @Size(max = 255, message = "Customer name must not exceed 255 characters") String customerName,
            @RequestParam("customer_slug") @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Customer slug must be in kebab-case format") @Size(max = 100, message = "Customer slug must not exceed 100 characters") String customerSlug) {
        
        logger.info("Received tracking number generation request for customer: {} ({})", 
                   customerName, customerId);
        logger.debug("Request parameters - origin: {}, destination: {}, weight: {}, created_at: {}", 
                    originCountryId, destinationCountryId, weight, createdAt);
        
        try {
            // Parse the timestamp
            java.time.OffsetDateTime parsedCreatedAt;
            try {
                parsedCreatedAt = java.time.OffsetDateTime.parse(createdAt);
            } catch (java.time.format.DateTimeParseException e) {
                logger.warn("Invalid timestamp format: {}", createdAt);
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Invalid timestamp format", "created_at must be in RFC 3339 format"));
            }
            
            // Parse the customer ID
            UUID parsedCustomerId;
            try {
                parsedCustomerId = UUID.fromString(customerId);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid UUID format: {}", customerId);
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Invalid UUID format", "customer_id must be a valid UUID"));
            }
            
            // Create the request object
            TrackingNumberRequest request = new TrackingNumberRequest(
                originCountryId,
                destinationCountryId,
                weight,
                parsedCreatedAt,
                parsedCustomerId,
                customerName,
                customerSlug
            );
            
            // Validate the request
            if (!request.isValid()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validation Failed", "Invalid request parameters"));
            }
            
            TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);
            
            logger.info("Successfully generated tracking number: {} for customer: {}", 
                       response.trackingNumber(), customerName);
            
            return ResponseEntity.ok(response);
            
        } catch (TrackingNumberService.TrackingNumberGenerationException e) {
            logger.error("Failed to generate tracking number for customer: {} - {}", 
                        customerName, e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to generate tracking number", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error generating tracking number for customer: {}", 
                        customerName, e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "An unexpected error occurred"));
        }
    }
    
    /**
     * Health check endpoint.
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "tracking-number-generator",
            "timestamp", java.time.OffsetDateTime.now().toString()
        ));
    }
    
    /**
     * Debug endpoint to test parameter binding.
     * 
     * @param originCountryId the origin country ID
     * @param destinationCountryId the destination country ID
     * @param weight the weight
     * @param createdAt the created at timestamp
     * @param customerId the customer ID
     * @param customerName the customer name
     * @param customerSlug the customer slug
     * @return ResponseEntity with debug information
     */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug(
            @RequestParam(value = "origin_country_id", required = false) String originCountryId,
            @RequestParam(value = "destination_country_id", required = false) String destinationCountryId,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "created_at", required = false) String createdAt,
            @RequestParam(value = "customer_id", required = false) String customerId,
            @RequestParam(value = "customer_name", required = false) String customerName,
            @RequestParam(value = "customer_slug", required = false) String customerSlug) {
        
        Map<String, Object> debug = new java.util.HashMap<>();
        debug.put("originCountryId", originCountryId);
        debug.put("destinationCountryId", destinationCountryId);
        debug.put("weight", weight);
        debug.put("createdAt", createdAt);
        debug.put("customerId", customerId);
        debug.put("customerName", customerName);
        debug.put("customerSlug", customerSlug);
        debug.put("timestamp", java.time.OffsetDateTime.now().toString());
        debug.put("version", "1.0.0");
        
        return ResponseEntity.ok(debug);
    }
    
    /**
     * Creates an error response with the given message.
     * 
     * @param error the error type
     * @param message the error message
     * @return TrackingNumberResponse with error information
     */
    private TrackingNumberResponse createErrorResponse(String error, String message) {
        return new TrackingNumberResponse(
            "ERROR",
            java.time.OffsetDateTime.now(),
            UUID.randomUUID(),
            null,
            error + ": " + message
        );
    }
}