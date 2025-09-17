package com.tracking.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tracking.domain.TrackingNumberRequest;
import com.tracking.domain.TrackingNumberResponse;
import com.tracking.repository.TrackingNumberRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Unit tests for TrackingNumberService.
 */
@ExtendWith(MockitoExtension.class)
class TrackingNumberServiceTest {
    
    @Mock
    private TrackingNumberRepository trackingNumberRepository;
    
    private MeterRegistry meterRegistry;
    
    private TrackingNumberService trackingNumberService;
    
    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trackingNumberService = new TrackingNumberService(trackingNumberRepository, meterRegistry, 3);
    }
    
    @Test
    void generateTrackingNumber_ShouldReturnValidResponse_WhenRequestIsValid() {
        // Given
        TrackingNumberRequest request = createValidRequest();
        when(trackingNumberRepository.existsByTrackingNumber(anyString())).thenReturn(false);
        when(trackingNumberRepository.save(any())).thenReturn(null);
        
        // When
        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.trackingNumber()).matches("^[A-Z0-9]{8,16}$");
        assertThat(response.createdAt()).isEqualTo(request.createdAt());
        assertThat(response.customerId()).isEqualTo(request.customerId());
        assertThat(response.customerName()).isEqualTo(request.customerName());
        
        verify(trackingNumberRepository).existsByTrackingNumber(anyString());
        verify(trackingNumberRepository).save(any());
    }
    
    @Test
    void generateTrackingNumber_ShouldRetryOnCollision_WhenTrackingNumberExists() {
        // Given
        TrackingNumberRequest request = createValidRequest();
        when(trackingNumberRepository.existsByTrackingNumber(anyString()))
                .thenReturn(true)  // First attempt fails
                .thenReturn(false); // Second attempt succeeds
        when(trackingNumberRepository.save(any())).thenReturn(null);
        
        // When
        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.trackingNumber()).matches("^[A-Z0-9]{8,16}$");
        
        verify(trackingNumberRepository, atLeast(2)).existsByTrackingNumber(anyString());
        verify(trackingNumberRepository).save(any());
    }
    
    @Test
    void generateTrackingNumber_ShouldThrowException_WhenMaxRetriesExceeded() {
        // Given
        TrackingNumberRequest request = createValidRequest();
        when(trackingNumberRepository.existsByTrackingNumber(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> trackingNumberService.generateTrackingNumber(request))
                .isInstanceOf(TrackingNumberService.TrackingNumberGenerationException.class)
                .hasMessageContaining("Unable to generate unique tracking number after 3 attempts");
        
        verify(trackingNumberRepository, times(3)).existsByTrackingNumber(anyString());
        verify(trackingNumberRepository, never()).save(any());
    }
    
    @Test
    void generateTrackingNumber_ShouldThrowException_WhenSaveFails() {
        // Given
        TrackingNumberRequest request = createValidRequest();
        when(trackingNumberRepository.existsByTrackingNumber(anyString())).thenReturn(false);
        when(trackingNumberRepository.save(any())).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThatThrownBy(() -> trackingNumberService.generateTrackingNumber(request))
                .isInstanceOf(TrackingNumberService.TrackingNumberGenerationException.class)
                .hasMessageContaining("Failed to generate tracking number: Database error");
        
        verify(trackingNumberRepository).existsByTrackingNumber(anyString());
        verify(trackingNumberRepository).save(any());
    }
    
    @Test
    void generateTrackingNumber_ShouldHandleEdgeCaseWeights_WhenWeightIsAtBoundaries() {
        // Given
        TrackingNumberRequest request1 = new TrackingNumberRequest(
                "MY", "ID", 0.001, OffsetDateTime.now(), UUID.randomUUID(), "Test", "test");
        TrackingNumberRequest request2 = new TrackingNumberRequest(
                "MY", "ID", 999.999, OffsetDateTime.now(), UUID.randomUUID(), "Test", "test");
        
        when(trackingNumberRepository.existsByTrackingNumber(anyString())).thenReturn(false);
        when(trackingNumberRepository.save(any())).thenReturn(null);
        
        // When & Then
        TrackingNumberResponse response1 = trackingNumberService.generateTrackingNumber(request1);
        TrackingNumberResponse response2 = trackingNumberService.generateTrackingNumber(request2);
        
        assertThat(response1).isNotNull();
        assertThat(response2).isNotNull();
        assertThat(response1.trackingNumber()).matches("^[A-Z0-9]{8,16}$");
        assertThat(response2.trackingNumber()).matches("^[A-Z0-9]{8,16}$");
    }
    
    private TrackingNumberRequest createValidRequest() {
        return new TrackingNumberRequest(
                "MY",
                "ID",
                1.234,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                "Test Customer",
                "test-customer"
        );
    }
}
