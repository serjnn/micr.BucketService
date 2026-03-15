package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String description, BigDecimal price,
                         String category) {
}
