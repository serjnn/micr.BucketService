package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.CompleteProductDto;
import com.serjnn.BucketService.dtos.OrderDto;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.model.Bucket;
import com.serjnn.BucketService.model.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {
    private final BucketRepository bucketRepository;
    private final RestTemplate restTemplate;
    private final BucketItemRepository bucketItemRepository;
    private final ProductsOfBucketFetcher productsOfBucketFetcher;


    public List<CompleteProductDto> getCompleteProducts(Long clientId) {
        log.info("Fetching complete products for client {}", clientId);
        Bucket bucket = findOrCreateBucket(clientId);
        List<BucketItem> bucketItems = bucketItemRepository.findAllByBucketId(bucket.id());

        if (bucketItems.isEmpty()) {
            log.info("Bucket is empty for client {}", clientId);
            return new ArrayList<>();
        }

        List<Long> productIds = bucketItems.stream().map(BucketItem::productId).toList();

        log.info("Calling product service to fetch details for {} products", productIds.size());
        List<ProductDto> productDtos = productsOfBucketFetcher.retrieveProducts(productIds);



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
                    log.info("Bucket not found for client {}. Creating new bucket.", clientId);
                    try {
                        return bucketRepository.createBucket(clientId);
                    } catch (Exception e) {
                        log.warn("Conflict while creating bucket for client {}. Attempting to find again.", clientId);
                        // In case of a race condition where another thread created it
                        return bucketRepository.findBucketByClientId(clientId)
                                .orElseThrow(() -> {
                                    log.error("Failed to create or find bucket for client {}", clientId);
                                    return new RuntimeException("Bucket could not be found or created", e);
                                });
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
        log.info("Restoring bucket for client {} from order {}", orderDTO.clientId(), orderDTO.orderId());
        Bucket bucket = findOrCreateBucket(orderDTO.clientId());
        if (orderDTO.items() != null && !orderDTO.items().isEmpty()) {
            log.debug("Restoring {} items to bucket {}", orderDTO.items().size(), bucket.id());
            bucketItemRepository.restoreBatchInsert(bucket.id(), orderDTO.items());
        } else {
            log.info("No items to restore for client {}", orderDTO.clientId());
        }
    }

    public void addProduct(Long clientId, Long productId) {
        log.info("Adding product {} to bucket for client {}", productId, clientId);
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.addProduct(bucket.id(), productId, 1);
        log.debug("Product {} added to bucket {}", productId, bucket.id());
    }

    public void removeProductFromBucket(Long clientId, Long productId) {
        log.info("Removing product {} from bucket for client {}", productId, clientId);
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.decrementOrDelete(bucket.id(), productId);
        log.debug("Product {} removed or decremented in bucket {}", productId, bucket.id());
    }

    public void clearBucket(Long clientId) {
        log.info("Clearing bucket for client {}", clientId);
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.clearBucket(bucket.id());
        log.debug("Bucket {} cleared", bucket.id());
    }
}
