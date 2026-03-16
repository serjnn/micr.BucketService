package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDto(UUID orderId, Long clientId, List<BucketItemRestoredDto> items, BigDecimal totalSum) {
}
