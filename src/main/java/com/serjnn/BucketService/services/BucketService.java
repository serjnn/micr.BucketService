package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.CompleteProductDto;
import com.serjnn.BucketService.dtos.OrderDto;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.model.Bucket;
import com.serjnn.BucketService.model.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final BucketRepository bucketRepository;
    private final RestTemplate restTemplate;
    private final BucketItemRepository bucketItemRepository;

    public List<CompleteProductDto> getCompleteProducts(Long clientId) {
        Bucket bucket = findOrCreateBucket(clientId);
        List<BucketItem> bucketItems = bucketItemRepository.findAllByBucketId(bucket.id());

        if (bucketItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> productIds = bucketItems.stream().map(BucketItem::productId).toList();
        Map<String, List<Long>> requestBody = new HashMap<>();
        requestBody.put("ids", productIds);

        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                "http://product/api/v1/all/by-ids",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );

        if (response == null || response.getBody() == null) {
            return new ArrayList<>();
        }

        List<ProductDto> productDtos = response.getBody();


        return productDtos.stream().map(product -> {
            int quantity = bucketItems.stream()
                    .filter(item -> item.productId().equals(product.id()))
                    .findFirst()
                    .map(BucketItem::quantity)
                    .orElse(0);

            return new CompleteProductDto(
                    product.id(),
                    quantity,
                    product.name(),
                    product.description(),
                    product.price(),
                    product.category()
            );
        }).toList();
    }

    private Bucket findOrCreateBucket(Long clientId) {
        return bucketRepository.findBucketByClientId(clientId)
                .orElseGet(() -> {
                    try {
                        return bucketRepository.createBucket(clientId);
                    } catch (Exception e) {
                        // In case of a race condition where another thread created it
                        return bucketRepository.findBucketByClientId(clientId)
                                .orElseThrow(() -> new RuntimeException(
                                        "Bucket could not be found or created", e));
                    }
                });
    }

    /**
     * Restores the bucket items from a previous order.
     * This method is part of the SAGA pattern logic for order compensation.
     *
     * @param orderDTO The order data transfer object containing items to restore.
     */
    public void restore(OrderDto orderDTO) {
        Bucket bucket = findOrCreateBucket(orderDTO.clientId());
        if (orderDTO.items() != null && !orderDTO.items().isEmpty()) {
            bucketItemRepository.restoreBatchInsert(bucket.id(), orderDTO.items());
        }
    }

    public void addProduct(Long clientId, Long productId) {
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.addProduct(bucket.id(), productId, 1);
    }

    public void removeProductFromBucket(Long clientId, Long productId) {
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.decrementOrDelete(bucket.id(), productId);
    }

    public void clearBucket(Long clientId) {
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.clearBucket(bucket.id());
    }
}
