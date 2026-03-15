package com.serjnn.BucketService.dtos;

public record BucketItem(
    long id,
    long bucketId,
    long productId,
    int quantity
) {
    public BucketItem(long bucketId, long productId, int quantity) {
        this(0L, bucketId, productId, quantity);
    }
}
