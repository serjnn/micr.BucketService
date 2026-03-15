package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.dtos.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BucketRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Bucket> findBucketByClientId(Long clientId) {
        return jdbcTemplate.query("SELECT * FROM bucket WHERE client_id = ?",
                        new DataClassRowMapper<>(Bucket.class), clientId)
                .stream().findFirst();
    }

    public void createBucket(long clientId) {
        jdbcTemplate.update("INSERT INTO bucket (client_id) VALUES (?)", clientId);
    }
}
