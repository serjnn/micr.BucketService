package com.serjnn.BucketService.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDTO(UUID orderId, Long clientId, List<BucketItemDTO> items, BigDecimal totalSum) {
}
