package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.dtos.Bucket;
import com.serjnn.BucketService.dtos.BucketItem;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final BucketRepository bucketRepository;
    private final RestTemplate restTemplate;
    private final BucketItemRepository bucketItemRepository;

    public List<CompleteProduct> getCompleteProducts(long clientId) {
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

        List<ProductDto> productDtos = response.getBody();
        if (productDtos == null) {
            return new ArrayList<>();
        }

        return productDtos.stream().map(product -> {
            int quantity = bucketItems.stream()
                    .filter(item -> item.productId() == product.id())
                    .findFirst()
                    .map(BucketItem::quantity)
                    .orElse(0);

            return new CompleteProduct(
                    product.id(),
                    quantity,
                    product.name(),
                    product.description(),
                    product.price(),
                    product.category()
            );
        }).toList();
    }

    private Bucket findOrCreateBucket(long clientId) {
        Bucket bucket;
        Optional<Bucket> existingBucket = bucketRepository.findBucketByClientId(clientId);
        if (existingBucket.isPresent()) {
            return existingBucket.get();
        } else {
            bucketRepository.createBucket(clientId);
            return bucketRepository.findBucketByClientId(clientId).orElseThrow();
        }
    } // todo make rep return saved bucke not to ask for it

    public void restore(OrderDTO orderDTO) {
        Bucket bucket = findOrCreateBucket(orderDTO.clientId());
        orderDTO.items().forEach(item -> {
            bucketItemRepository.addProduct(bucket.id(), item.id(), item.quantity());
        });
    } //todo use batching so that we dont save smth in cycle

    public void addProduct(long clientId, long productId) {
        Bucket bucket = findOrCreateBucket(clientId);
        Optional<BucketItem> existingItem = bucketItemRepository.findByBucketIdAndProductId(bucket.id(), productId);

        if (existingItem.isPresent()) {
            BucketItem item = existingItem.get();
            bucketItemRepository.updateQuantity(bucket.id(), productId, item.quantity() + 1);
        } else {
            bucketItemRepository.addProduct(bucket.id(), productId, 1);
        }
    }

    public void removeProductFromBucket(long clientId, long productId) {
        Bucket bucket = findOrCreateBucket(clientId);
        Optional<BucketItem> existingItem = bucketItemRepository.findByBucketIdAndProductId(bucket.id(), productId);

        if (existingItem.isPresent()) {
            BucketItem item = existingItem.get();
            if (item.quantity() > 1) {
                bucketItemRepository.updateQuantity(bucket.id(), productId, item.quantity() - 1);
            } else {
                bucketItemRepository.deleteProduct(bucket.id(), productId);
            }
        }
    }

    public void clearBucket(long clientId) {
        Bucket bucket = findOrCreateBucket(clientId);
        bucketItemRepository.clearBucket(bucket.id());
    }
}
