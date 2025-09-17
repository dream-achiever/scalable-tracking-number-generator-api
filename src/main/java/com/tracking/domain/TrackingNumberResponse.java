package com.tracking.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for tracking number generation.
 * 
 * Contains the generated tracking number and metadata about the request.
 */
public record TrackingNumberResponse(
    
    @JsonProperty("tracking_number")
    String trackingNumber,
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime createdAt,
    
    @JsonProperty("request_id")
    UUID requestId,
    
    @JsonProperty("customer_id")
    UUID customerId,
    
    @JsonProperty("customer_name")
    String customerName
) {
    
    /**
     * Creates a response with minimal required fields.
     * 
     * @param trackingNumber the generated tracking number
     * @param createdAt the creation timestamp
     * @return TrackingNumberResponse instance
     */
    public static TrackingNumberResponse of(String trackingNumber, OffsetDateTime createdAt) {
        return new TrackingNumberResponse(trackingNumber, createdAt, null, null, null);
    }
    
    /**
     * Creates a response with all fields.
     * 
     * @param trackingNumber the generated tracking number
     * @param createdAt the creation timestamp
     * @param requestId the request ID for tracing
     * @param customerId the customer ID
     * @param customerName the customer name
     * @return TrackingNumberResponse instance
     */
    public static TrackingNumberResponse of(String trackingNumber, OffsetDateTime createdAt, 
                                          UUID requestId, UUID customerId, String customerName) {
        return new TrackingNumberResponse(trackingNumber, createdAt, requestId, customerId, customerName);
    }
}