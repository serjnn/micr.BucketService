package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dtos.CompleteProductDto;
import com.serjnn.BucketService.dtos.OrderDto;
import com.serjnn.BucketService.services.BucketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets")
@Tag(name = "Bucket Controller", description = "API for managing user buckets")
public class BucketController {

    private final BucketService bucketService;

    @PostMapping("/{clientId}/products/{productId}")
    @Operation(summary = "Add a product to the bucket", description = "Adds a product to the specified client's bucket")
    public void addProductToBucket(@PathVariable("clientId") Long clientId,
                                   @PathVariable("productId") Long productId) {
        bucketService.addProduct(clientId, productId);
    }

    @DeleteMapping("/{clientId}/products/{productId}")
    @Operation(summary = "Remove a product from the bucket", description = "Removes a product from the specified client's bucket")
    public void removeProductFromBucket(@PathVariable Long clientId,
                                        @PathVariable Long productId) {
        bucketService.removeProductFromBucket(clientId, productId);
    }

    @GetMapping("/{clientId}")
    @Operation(summary = "Get the complete bucket", description = "Retrieves all products in the specified client's bucket")
    public List<CompleteProductDto> getBucketContents(@PathVariable("clientId") Long clientId) {
        return bucketService.getCompleteProducts(clientId);
    }

    @DeleteMapping("/{clientId}")
    @Operation(summary = "Clear the bucket", description = "Removes all products from the specified client's bucket")
    public void clearBucket(@PathVariable Long clientId) {
        bucketService.clearBucket(clientId);
    }

    @PostMapping("/restore")
    @Operation(summary = "Restore the bucket (SAGA Compensation)", description = "Restores the bucket from a previous order as part of SAGA compensation logic.")
    public void restoreBucket(@RequestBody OrderDto orderDTO) {
        bucketService.restore(orderDTO);
    }
}
