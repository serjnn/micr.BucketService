package com.serjnn.BucketService.controller;

import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.services.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BucketController {

    private final BucketService bucketService;

    @GetMapping("/addProduct/{clientId}/{productId}")
    public void add(@PathVariable("clientId") long clientId,
                    @PathVariable("productId") long productId) {
        bucketService.addProduct(clientId, productId);
    }

    @GetMapping("/removeProduct/{clientId}/{productId}")
    public void remove(@PathVariable long clientId,
                       @PathVariable long productId) {
        bucketService.removeProductFromBucket(clientId, productId);
    }

    @GetMapping("/getCompleteBucket/{clientId}")
    public List<CompleteProduct> complete(@PathVariable("clientId") long clientId) {
        return bucketService.getCompleteProducts(clientId);
    }

    @PostMapping("/clearBucket")
    public void clear(@RequestBody long clientId) {
        bucketService.clearBucket(clientId);
    }

    @PostMapping("/restoreBucket")
    public void restore(@RequestBody OrderDTO orderDTO) {
        bucketService.restore(orderDTO);
    }
}
