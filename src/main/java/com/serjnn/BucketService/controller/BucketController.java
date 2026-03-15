package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.services.BucketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Bucket Controller", description = "API for managing user buckets")
public class BucketController {

    private final BucketService bucketService;

    @GetMapping("/addProduct/{clientId}/{productId}")
    @Operation(summary = "Add a product to the bucket", description = "Adds a product to the specified client's bucket")
    public void add(@PathVariable("clientId") long clientId,
                    @PathVariable("productId") long productId) {
        bucketService.addProduct(clientId, productId);
    }

    @GetMapping("/removeProduct/{clientId}/{productId}")
    @Operation(summary = "Remove a product from the bucket", description = "Removes a product from the specified client's bucket")
    public void remove(@PathVariable long clientId,
                       @PathVariable long productId) {
        bucketService.removeProductFromBucket(clientId, productId);
    }

    @GetMapping("/getCompleteBucket/{clientId}")
    @Operation(summary = "Get the complete bucket", description = "Retrieves all products in the specified client's bucket")
    public List<CompleteProduct> complete(@PathVariable("clientId") long clientId) {
        return bucketService.getCompleteProducts(clientId);
    }

    @PostMapping("/clearBucket")
    @Operation(summary = "Clear the bucket", description = "Removes all products from the specified client's bucket")
    public void clear(@RequestBody long clientId) {
        bucketService.clearBucket(clientId);
    }

    @PostMapping("/restoreBucket")
    @Operation(summary = "Restore the bucket", description = "Restores the bucket from a previous order")
    public void restore(@RequestBody OrderDTO orderDTO) {
        bucketService.restore(orderDTO);
    }
}
