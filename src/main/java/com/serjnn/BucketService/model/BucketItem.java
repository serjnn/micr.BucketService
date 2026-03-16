package com.serjnn.BucketService.model;

public record BucketItem(
    Long id,
    Long bucketId,
    Long productId,
    Integer quantity
) {}

