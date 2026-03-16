package com.serjnn.BucketService.integration;

import com.serjnn.BucketService.BucketServiceApplication;
import com.serjnn.BucketService.dtos.CompleteProductDto;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.services.BucketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BucketServiceApplication.class)
@Testcontainers
public class BucketServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private BucketService bucketService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertThat(bucketService).isNotNull();
    }

    @Test
    void shouldAddProductAndGetCompleteProducts() {
        // Given
        long clientId = 1L;
        long productId = 100L;

        // Mock RestTemplate response
        ProductDto mockProduct = new ProductDto(productId, "Test Product", "Test Desc", BigDecimal.valueOf(10.0), "Test Category");
        
        when(restTemplate.exchange(
                eq("http://product/api/v1/all/by-ids"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(mockProduct)));

        // When
        bucketService.addProduct(clientId, productId);
        List<CompleteProductDto> completeProductDtos = bucketService.getCompleteProducts(clientId);

        // Then
        assertThat(completeProductDtos).isNotEmpty();
        assertThat(completeProductDtos).hasSize(1);
        CompleteProductDto product = completeProductDtos.get(0);
        assertThat(product.id()).isEqualTo(productId);
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.quantity()).isEqualTo(1);
    }
}
