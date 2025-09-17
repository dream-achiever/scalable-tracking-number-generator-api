package com.tracking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tracking.domain.TrackingNumber;

import jakarta.persistence.LockModeType;

/**
 * Repository interface for TrackingNumber entities.
 * 
 * Provides data access methods with proper locking for concurrent access
 * and thread-safe operations.
 */
@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, Long> {
    
    /**
     * Finds a tracking number by its unique tracking number string.
     * Uses pessimistic locking to prevent concurrent access issues.
     * 
     * @param trackingNumber the tracking number to find
     * @return Optional containing the TrackingNumber if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TrackingNumber t WHERE t.trackingNumber = :trackingNumber")
    Optional<TrackingNumber> findByTrackingNumberWithLock(@Param("trackingNumber") String trackingNumber);
    
    /**
     * Checks if a tracking number exists.
     * 
     * @param trackingNumber the tracking number to check
     * @return true if the tracking number exists, false otherwise
     */
    boolean existsByTrackingNumber(String trackingNumber);
    
    /**
     * Finds tracking numbers by customer ID.
     * 
     * @param customerId the customer UUID
     * @return list of tracking numbers for the customer
     */
    @Query("SELECT t FROM TrackingNumber t WHERE t.customerId = :customerId ORDER BY t.createdAt DESC")
    java.util.List<TrackingNumber> findByCustomerId(@Param("customerId") UUID customerId);
    
    /**
     * Counts tracking numbers by customer ID.
     * 
     * @param customerId the customer UUID
     * @return count of tracking numbers for the customer
     */
    long countByCustomerId(UUID customerId);
}