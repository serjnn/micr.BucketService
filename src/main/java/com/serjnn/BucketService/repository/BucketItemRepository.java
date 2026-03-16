package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.model.BucketItem;
import com.serjnn.BucketService.dtos.BucketItemRestoredDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BucketItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<BucketItem> findAllByBucketId(Long bucketId) {
        return jdbcTemplate.query("SELECT * FROM bucket_item WHERE bucket_id = ?", new DataClassRowMapper<>(BucketItem.class), bucketId);
    }

    public Optional<BucketItem> findByBucketIdAndProductId(Long bucketId, Long productId) {
        return jdbcTemplate.query("SELECT * FROM bucket_item WHERE bucket_id = ? AND product_id = ?", new DataClassRowMapper<>(BucketItem.class), bucketId, productId)
                .stream().findFirst();
    }

    public void deleteAll(List<BucketItem> items) {
        for (BucketItem item : items) {
            jdbcTemplate.update("DELETE FROM bucket_item WHERE id = ?", item.id());
        }
    }

    public void addProduct(long bucketId, long productId, int quantity) {
        jdbcTemplate.update("INSERT INTO bucket_item (bucket_id, product_id, quantity) VALUES (?, ?, ?)", bucketId, productId, quantity);
    }

    public void restoreBatchInsert(long bucketId, List<BucketItemRestoredDto> items) {
        jdbcTemplate.batchUpdate("INSERT INTO bucket_item (bucket_id, product_id, quantity) VALUES (?, ?, ?)",
                items,
                items.size(),
                (ps, item) -> {
                    ps.setLong(1, bucketId);
                    ps.setLong(2, item.id());
                    ps.setInt(3, item.quantity());
                });
    }
    
    public void updateQuantity(long bucketId, long productId, int quantity) {
        jdbcTemplate.update("UPDATE bucket_item SET quantity = ? WHERE bucket_id = ? AND product_id = ?", quantity, bucketId, productId);
    }

    public void deleteProduct(long bucketId, long productId) {
        jdbcTemplate.update("DELETE FROM bucket_item WHERE bucket_id = ? AND product_id = ?", bucketId, productId);
    }

    public void clearBucket(long bucketId) {
        jdbcTemplate.update("DELETE FROM bucket_item WHERE bucket_id = ?", bucketId);
    }
}
