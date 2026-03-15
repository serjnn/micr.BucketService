package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;

public record BucketItemDTO(long id, String name, int quantity, BigDecimal price) {
}
