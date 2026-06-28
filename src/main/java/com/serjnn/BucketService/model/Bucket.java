package com.serjnn.BucketService.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("bucket")
public class Bucket {
    @Id
    private long id;

    private long clientId;

    public Bucket(long clientId) {
        this.clientId = clientId;
    }
}
