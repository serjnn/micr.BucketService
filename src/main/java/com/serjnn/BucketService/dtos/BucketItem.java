package com.serjnn.BucketService.dtos;

public record BucketItem(
    Long id,
    Long bucketId,
    Long productId,
    Integer quantity
) {
    public BucketItem(Long bucketId, Long productId, Integer quantity) {
        this(0L, bucketId, productId, quantity);
    }
}
