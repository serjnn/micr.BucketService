package com.serjnn.BucketService.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("bucket_item")
public class BucketItem {
    @Id
    private long id;
    private long bucketId;
    private long productId;
    private int quantity;

    public BucketItem( Long bucketId, Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.bucketId = bucketId;
    }

    @Override
    public String toString() {
        return "BucketItem{" +
                "id=" + id +
                ", bucketId=" + bucketId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
