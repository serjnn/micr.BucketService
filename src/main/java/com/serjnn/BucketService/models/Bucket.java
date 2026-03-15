package com.serjnn.BucketService.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Bucket {
    private long id;
    private long clientId;

    public Bucket(long clientId) {
        this.clientId = clientId;
    }

    public Bucket(long id, long clientId) {
        this.id = id;
        this.clientId = clientId;
    }
}
