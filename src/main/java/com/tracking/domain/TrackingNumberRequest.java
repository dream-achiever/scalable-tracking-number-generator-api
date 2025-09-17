package com.tracking.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for generating tracking numbers.
 * 
 * Contains all the required parameters for tracking number generation
 * including validation constraints.
 */
public record TrackingNumberRequest(
    
    @NotBlank(message = "Origin country ID is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Origin country ID must be a valid ISO 3166-1 alpha-2 code")
    String originCountryId,
    
    @NotBlank(message = "Destination country ID is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Destination country ID must be a valid ISO 3166-1 alpha-2 code")
    String destinationCountryId,
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be at least 0.001 kg")
    @DecimalMax(value = "999.999", message = "Weight must not exceed 999.999 kg")
    @Digits(integer = 3, fraction = 3, message = "Weight must have at most 3 decimal places")
    Double weight,
    
    @NotNull(message = "Created at timestamp is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime createdAt,
    
    @NotNull(message = "Customer ID is required")
    UUID customerId,
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    String customerName,
    
    @NotBlank(message = "Customer slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", 
             message = "Customer slug must be in kebab-case format")
    @Size(max = 100, message = "Customer slug must not exceed 100 characters")
    String customerSlug
) {
    
    /**
     * Validates that the request data is consistent.
     * 
     * @return true if the request is valid, false otherwise
     */
    public boolean isValid() {
        return originCountryId != null && destinationCountryId != null &&
               weight != null && weight > 0 && weight <= 999.999 &&
               createdAt != null && customerId != null &&
               customerName != null && !customerName.trim().isEmpty() &&
               customerSlug != null && !customerSlug.trim().isEmpty();
    }
}