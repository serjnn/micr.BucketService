package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.dtos.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
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


    public Bucket createBucket(Long clientId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO bucket (client_id) VALUES (?)",
                    new String[]{"id"}
            );
            ps.setLong(1, clientId);
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return new Bucket(generatedId, clientId);
    }
}
