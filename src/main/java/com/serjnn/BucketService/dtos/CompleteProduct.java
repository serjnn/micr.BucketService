package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;

public record CompleteProduct(long id, int quantity, String name, String description, BigDecimal price, String category) {
}
