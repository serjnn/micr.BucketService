package com.serjnn.BucketService;

import com.serjnn.BucketService.model.Bucket;
import com.serjnn.BucketService.model.BucketItem;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import com.serjnn.BucketService.service.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Disabled by default because running Testcontainers requires a local Docker daemon (e.g. Docker Desktop) to be active.")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.sql.init.mode=always"
})
public class BucketServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private BucketService bucketService;

    @Autowired
    private BucketRepository bucketRepository;

    @Autowired
    private BucketItemRepository bucketItemRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        bucketItemRepository.deleteAll().block();
        bucketRepository.deleteAll().block();
    }

    @Test
    public void testAddAndRemoveProductInBucket() {
        // Add a product to the bucket
        webTestClient.post()
                .uri("/api/v1/buckets/100/products/50")
                .exchange()
                .expectStatus().isOk();

        // Verify it was stored in the database
        Bucket bucket = bucketRepository.findBucketByClientId(100L).block();
        assertThat(bucket).isNotNull();

        List<BucketItem> items = bucketItemRepository.findAllByBucketId(bucket.getId()).collectList().block();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getProductId()).isEqualTo(50L);
        assertThat(items.get(0).getQuantity()).isEqualTo(1);

        // Add it again (should increment quantity)
        webTestClient.post()
                .uri("/api/v1/buckets/100/products/50")
                .exchange()
                .expectStatus().isOk();

        items = bucketItemRepository.findAllByBucketId(bucket.getId()).collectList().block();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(2);

        // Remove the product (should decrement quantity)
        webTestClient.delete()
                .uri("/api/v1/buckets/100/products/50")
                .exchange()
                .expectStatus().isOk();

        items = bucketItemRepository.findAllByBucketId(bucket.getId()).collectList().block();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(1);
    }
}
