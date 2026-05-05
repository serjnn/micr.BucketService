package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.ProductDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductsOfBucketFetcher {

    private final RestTemplate restTemplate;

    @Value("${services.product.url}")
    private String productServiceUrl;

    @Retry(name = "productService", fallbackMethod = "fallbackRetrieveProducts")
    public List<ProductDto> retrieveProducts(List<Long> productIds) {
        Map<String, List<Long>> requestBody = new HashMap<>();
        requestBody.put("ids", productIds);

        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                productServiceUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<List<ProductDto>>() {
                }
        );

        List<ProductDto> result = response.getBody();
        if (response == null || result == null) {
            log.warn("Received empty response from Product Service for product ids: {}",
                    productIds);
            return new ArrayList<>();
        }
        log.info("Successfully retrieved {} products from Product Service", result.size());
        return result;


    }

    public List<ProductDto> fallbackRetrieveProducts(List<Long> productIds, Exception e) {
        log.error("Error while retrieving products from Product Service");
        return new ArrayList<>();
    }
}
