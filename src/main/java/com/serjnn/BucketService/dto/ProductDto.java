package com.serjnn.BucketService.dto;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductDto {

    private long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;


}
