package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;

public record BucketItemRestoredDto(Long id, String name, Integer quantity, BigDecimal price) {
}
