package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;

public record CompleteProductDto(Long id, Integer quantity, String name, String description, BigDecimal price, String category) {
}
