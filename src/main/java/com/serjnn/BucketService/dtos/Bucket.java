package com.serjnn.BucketService.dtos;

public record Bucket(long id, long clientId) {


    public Bucket(long clientId) {
        this(0L, clientId);
    }
}
