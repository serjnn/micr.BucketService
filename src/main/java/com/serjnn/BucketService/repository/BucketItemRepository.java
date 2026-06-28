package com.serjnn.BucketService.repository;

import com.serjnn.BucketService.model.BucketItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BucketItemRepository extends ReactiveCrudRepository<BucketItem, Long> {

    Flux<BucketItem> findAllByBucketId(Long bucketId);

    Mono<Void> deleteAll(List<BucketItem> items);

    Mono<BucketItem> findByBucketId(long bucketId);


}