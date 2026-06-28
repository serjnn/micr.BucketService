package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dto.CompleteProduct;
import com.serjnn.BucketService.dto.OrderDTO;
import com.serjnn.BucketService.service.BucketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets")
@Tag(name = "Bucket API", description = "Endpoints for managing client shopping buckets")
public class BucketController {

    private final BucketService bucketService;

    @GetMapping("/{clientId}")
    @Operation(summary = "Get complete bucket content", description = "Retrieve all complete products in a client's bucket")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved bucket contents")
    public Flux<CompleteProduct> complete(@PathVariable("clientId") long clientId) {
        log.info("Request received: Get complete bucket for client ID {}", clientId);
        return bucketService.getCompleteProducts(clientId);
    }

    @PostMapping("/{clientId}/products/{productId}")
    @Operation(summary = "Add product to bucket", description = "Add a product (or increment its quantity) in a client's bucket")
    @ApiResponse(responseCode = "200", description = "Product successfully added to bucket")
    public Mono<Void> add(@PathVariable("clientId") long clientId,
                          @PathVariable("productId") long productId) {
        log.info("Request received: Add product ID {} to client ID {}'s bucket", productId, clientId);
        return bucketService.addProduct(clientId, productId);
    }

    @DeleteMapping("/{clientId}/products/{productId}")
    @Operation(summary = "Remove product from bucket", description = "Decrement the quantity of a product or remove it entirely if quantity reaches 0")
    @ApiResponse(responseCode = "200", description = "Product successfully removed/decremented")
    public Mono<Void> remove(@PathVariable("clientId") long clientId,
                             @PathVariable("productId") long productId) {
        log.info("Request received: Remove product ID {} from client ID {}'s bucket", productId, clientId);
        return bucketService.removeProductFromBucket(clientId, productId);
    }

    @DeleteMapping("/{clientId}")
    @Operation(summary = "Clear bucket", description = "Remove all products and empty the client's bucket")
    @ApiResponse(responseCode = "200", description = "Bucket successfully cleared")
    public Mono<Void> clear(@PathVariable("clientId") long clientId) {
        log.info("Request received: Clear bucket for client ID {}", clientId);
        return bucketService.clearBucket(clientId);
    }

    @PostMapping("/restore")
    @Operation(summary = "Restore bucket content", description = "Repopulate the client's bucket using an existing order payload")
    @ApiResponse(responseCode = "200", description = "Bucket successfully restored")
    public Mono<Void> restore(@RequestBody OrderDTO orderDTO) {
        log.info("Request received: Restore bucket: {}", orderDTO);
        return bucketService.restore(orderDTO);
    }
}

