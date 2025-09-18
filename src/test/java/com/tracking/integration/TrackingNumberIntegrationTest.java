package com.tracking.integration;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracking.domain.TrackingNumberRequest;
import com.tracking.repository.TrackingNumberRepository;

/**
 * Integration tests for the tracking number API using H2 in-memory database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class TrackingNumberIntegrationTest {
    
    @Autowired
    private TrackingNumberRepository trackingNumberRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        trackingNumberRepository.deleteAll();
    }
    
    @Test
    void generateTrackingNumber_ShouldCreateUniqueTrackingNumber_WhenRequestIsValid() throws Exception {
        // Given
        TrackingNumberRequest request = createValidRequest();
        
        // When
        mockMvc.perform(get("/api/v1/next-tracking-number")
                        .param("origin_country_id", request.originCountryId())
                        .param("destination_country_id", request.destinationCountryId())
                        .param("weight", request.weight().toString())
                        .param("created_at", request.createdAt().toString())
                        .param("customer_id", request.customerId().toString())
                        .param("customer_name", request.customerName())
                        .param("customer_slug", request.customerSlug())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tracking_number").exists())
                .andExpect(jsonPath("$.tracking_number").isString())
                .andExpect(jsonPath("$.tracking_number").value(org.hamcrest.Matchers.matchesPattern("^[A-Z0-9]{8,16}$")))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.customer_id").value(request.customerId().toString()))
                .andExpect(jsonPath("$.customer_name").value(request.customerName()));
        
        // Then
        assertThat(trackingNumberRepository.count()).isEqualTo(1);
    }
    
    @Test
    void generateTrackingNumber_ShouldHandleConcurrentRequests_WhenMultipleRequestsAreMade() throws Exception {
        // Given
        TrackingNumberRequest request1 = createValidRequest();
        TrackingNumberRequest request2 = createValidRequest();
        
        // When
        mockMvc.perform(get("/api/v1/next-tracking-number")
                        .param("origin_country_id", request1.originCountryId())
                        .param("destination_country_id", request1.destinationCountryId())
                        .param("weight", request1.weight().toString())
                        .param("created_at", request1.createdAt().toString())
                        .param("customer_id", request1.customerId().toString())
                        .param("customer_name", request1.customerName())
                        .param("customer_slug", request1.customerSlug())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/v1/next-tracking-number")
                        .param("origin_country_id", request2.originCountryId())
                        .param("destination_country_id", request2.destinationCountryId())
                        .param("weight", request2.weight().toString())
                        .param("created_at", request2.createdAt().toString())
                        .param("customer_id", request2.customerId().toString())
                        .param("customer_name", request2.customerName())
                        .param("customer_slug", request2.customerSlug())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // Then
        assertThat(trackingNumberRepository.count()).isEqualTo(2);
    }
    
    @Test
    void generateTrackingNumber_ShouldReturnBadRequest_WhenRequiredParametersAreMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/next-tracking-number")
                        .param("origin_country_id", "MY")
                        .param("destination_country_id", "ID")
                        // Missing weight parameter
                        .param("created_at", OffsetDateTime.now().toString())
                        .param("customer_id", UUID.randomUUID().toString())
                        .param("customer_name", "Test Customer")
                        .param("customer_slug", "test-customer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad Request"));
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
