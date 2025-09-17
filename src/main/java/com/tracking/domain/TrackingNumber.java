package com.tracking.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity representing a generated tracking number.
 * 
 * This entity stores the tracking number along with metadata about
 * the request that generated it.
 */
@Entity
@Table(name = "tracking_numbers", 
       uniqueConstraints = @UniqueConstraint(columnNames = "tracking_number"))
public class TrackingNumber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_number", nullable = false, unique = true, length = 16)
    private String trackingNumber;
    
    @Column(name = "origin_country_id", nullable = false, length = 2)
    private String originCountryId;
    
    @Column(name = "destination_country_id", nullable = false, length = 2)
    private String destinationCountryId;
    
    @Column(name = "weight", nullable = false)
    private Double weight;
    
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    
    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;
    
    @Column(name = "customer_slug", nullable = false, length = 100)
    private String customerSlug;
    
    @Column(name = "request_id")
    private UUID requestId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Default constructor for JPA
    protected TrackingNumber() {}
    
    /**
     * Creates a new TrackingNumber instance.
     * 
     * @param trackingNumber the unique tracking number
     * @param originCountryId the origin country ISO code
     * @param destinationCountryId the destination country ISO code
     * @param weight the package weight in kg
     * @param customerId the customer UUID
     * @param customerName the customer name
     * @param customerSlug the customer slug
     * @param requestId the request ID for tracing
     */
    public TrackingNumber(String trackingNumber, String originCountryId, String destinationCountryId,
                         Double weight, UUID customerId, String customerName, String customerSlug,
                         UUID requestId) {
        this.trackingNumber = trackingNumber;
        this.originCountryId = originCountryId;
        this.destinationCountryId = destinationCountryId;
        this.weight = weight;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerSlug = customerSlug;
        this.requestId = requestId;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getOriginCountryId() { return originCountryId; }
    public String getDestinationCountryId() { return destinationCountryId; }
    public Double getWeight() { return weight; }
    public UUID getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerSlug() { return customerSlug; }
    public UUID getRequestId() { return requestId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters for JPA
    protected void setId(Long id) { this.id = id; }
    protected void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    protected void setOriginCountryId(String originCountryId) { this.originCountryId = originCountryId; }
    protected void setDestinationCountryId(String destinationCountryId) { this.destinationCountryId = destinationCountryId; }
    protected void setWeight(Double weight) { this.weight = weight; }
    protected void setCustomerId(UUID customerId) { this.customerId = customerId; }
    protected void setCustomerName(String customerName) { this.customerName = customerName; }
    protected void setCustomerSlug(String customerSlug) { this.customerSlug = customerSlug; }
    protected void setRequestId(UUID requestId) { this.requestId = requestId; }
    protected void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    protected void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}