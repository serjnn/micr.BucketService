package com.serjnn.BucketService.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BucketItemDTO {

    private long id;
    private String name;
    private int quantity;
    private BigDecimal price;
}