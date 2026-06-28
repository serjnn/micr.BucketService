package com.serjnn.BucketService.integration;

import com.serjnn.BucketService.dto.ProductDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Disabled by default because running Testcontainers requires a local Docker daemon (e.g. Docker Desktop) to be active.")
public class BucketControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void shouldPerformFullBucketLifecycle() throws Exception {
        long clientId = 10L;
        long productId = 100L;

        // 1. Add product to bucket
        mockMvc.perform(post("/api/v1/buckets/{clientId}/products/{productId}", clientId, productId))
                .andExpect(status().isOk());

        // 2. Mock external product service response
        ProductDto mockProduct = new ProductDto(productId, "Controller Test Product", "Desc", BigDecimal.TEN, "Category");
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(mockProduct)));

        // 3. Get bucket contents and verify
        mockMvc.perform(get("/api/v1/buckets/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) productId)))
                .andExpect(jsonPath("$[0].quantity", is(1)))
                .andExpect(jsonPath("$[0].name", is("Controller Test Product")));

        // 4. Add same product again (increment quantity)
        mockMvc.perform(post("/api/v1/buckets/{clientId}/products/{productId}", clientId, productId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/buckets/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity", is(2)));

        // 5. Remove product from bucket (decrement quantity)
        mockMvc.perform(delete("/api/v1/buckets/{clientId}/products/{productId}", clientId, productId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/buckets/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity", is(1)));

        // 6. Clear bucket
        mockMvc.perform(delete("/api/v1/buckets/{clientId}", clientId))
                .andExpect(status().isOk());

        // Mock empty response for empty bucket
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/api/v1/buckets/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
