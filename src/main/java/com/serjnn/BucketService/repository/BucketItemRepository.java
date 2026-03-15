package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.dtos.BucketItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BucketItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<BucketItem> findAllByBucketId(Long bucketId) {
        return jdbcTemplate.query("SELECT * FROM bucket_item WHERE bucket_id = ?", new BeanPropertyRowMapper<>(BucketItem.class), bucketId);
    }

    public void deleteAll(List<BucketItem> items) {
        for (BucketItem item : items) {
            jdbcTemplate.update("DELETE FROM bucket_item WHERE id = ?", item.id());
        }
    }

    public void addProduct(long bucketId, long productId, int quantity) {
        jdbcTemplate.update("INSERT INTO bucket_item (bucket_id, product_id, quantity) VALUES (?, ?, ?)", bucketId, productId, quantity);
    }

    public void deleteProduct(long bucketId, long productId) {
        jdbcTemplate.update("DELETE FROM bucket_item WHERE bucket_id = ? AND product_id = ?", bucketId, productId);
    }

    public void clearBucket(long bucketId) {
        jdbcTemplate.update("DELETE FROM bucket_item WHERE bucket_id = ?", bucketId);
    }
}
