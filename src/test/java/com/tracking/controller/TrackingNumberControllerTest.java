package com.tracking.controller;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracking.domain.TrackingNumberRequest;
import com.tracking.domain.TrackingNumberResponse;
import com.tracking.service.TrackingNumberService;

/**
 * Unit tests for TrackingNumberController.
 */
@WebMvcTest(controllers = TrackingNumberController.class)
// Exclude the JPA autoconfiguration to prevent database connection attempts

@EnableAutoConfiguration(exclude = {JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
class TrackingNumberControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TrackingNumberService trackingNumberService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void generateTrackingNumber_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        // Given
        TrackingNumberRequest request = createValidRequest();
        TrackingNumberResponse response = new TrackingNumberResponse(
                "ABC12345",
                OffsetDateTime.now(),
                UUID.randomUUID(),
                request.customerId(),
                request.customerName()
        );
        
        when(trackingNumberService.generateTrackingNumber(any(TrackingNumberRequest.class)))
                .thenReturn(response);
        
        // When & Then
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
                .andExpect(jsonPath("$.tracking_number").value("ABC12345"))
                .andExpect(jsonPath("$.customer_id").value(request.customerId().toString()))
                .andExpect(jsonPath("$.customer_name").value(request.customerName()));
    }
    
    @Test
    void generateTrackingNumber_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/next-tracking-number")
                        .param("origin_country_id", "INVALID") // Invalid country code
                        .param("destination_country_id", "ID")
                        .param("weight", "1.234")
                        .param("created_at", OffsetDateTime.now().toString())
                        .param("customer_id", UUID.randomUUID().toString())
                        .param("customer_name", "Test Customer")
                        .param("customer_slug", "test-customer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }
    
    @Test
    void health_ShouldReturnOk_WhenCalled() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/next-tracking-number/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("tracking-number-generator"));
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
