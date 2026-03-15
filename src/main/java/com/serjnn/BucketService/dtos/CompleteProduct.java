package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;

public record CompleteProduct(Long id, Integer quantity, String name, String description, BigDecimal price, String category) {
}
