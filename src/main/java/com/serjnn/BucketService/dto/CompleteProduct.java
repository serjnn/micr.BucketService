package com.serjnn.BucketService.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CompleteProduct {

    private long id;
    private int quantity;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;

}
