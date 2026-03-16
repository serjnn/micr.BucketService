package com.serjnn.BucketService.dtos;

public record BucketItem(
    Long id,
    Long bucketId,
    Long productId,
    Integer quantity
) {}

