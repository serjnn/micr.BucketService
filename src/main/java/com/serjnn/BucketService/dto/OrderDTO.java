package com.serjnn.BucketService.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderDTO {

    private UUID orderId;

    private long clientId;

    private List<BucketItemDTO> items;

    private BigDecimal totalSum;

}