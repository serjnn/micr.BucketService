package com.serjnn.BucketService.services;

import com.serjnn.BucketService.dtos.Bucket;
import com.serjnn.BucketService.dtos.BucketItem;
import com.serjnn.BucketService.dtos.CompleteProduct;
import com.serjnn.BucketService.dtos.OrderDTO;
import com.serjnn.BucketService.dtos.BucketItemDTO;
import com.serjnn.BucketService.dtos.ProductDto;
import com.serjnn.BucketService.repository.BucketItemRepository;
import com.serjnn.BucketService.repository.BucketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {

    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BucketItemRepository bucketItemRepository;

    @InjectMocks
    private BucketService bucketService;

    private Bucket bucket;

    @BeforeEach
    void setUp() {
        bucket = new Bucket(1L, 1L);
    }

    @Test
    void getCompleteProducts_shouldReturnListOfCompleteProducts() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));
        BucketItem bucketItem = new BucketItem(1L, 1L, 1L, 2);
        when(bucketItemRepository.findAllByBucketId(1L)).thenReturn(List.of(bucketItem));
        ProductDto productDto = new ProductDto(1L, "Test Product", "Description", BigDecimal.TEN, "Category");
        ResponseEntity<List<ProductDto>> responseEntity = new ResponseEntity<>(List.of(productDto), HttpStatus.OK);
        when(restTemplate.exchange(
                eq("http://product/api/v1/all/by-ids"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<CompleteProduct> completeProducts = bucketService.getCompleteProducts(1L);

        assertNotNull(completeProducts);
        assertEquals(1, completeProducts.size());
        assertEquals(2, completeProducts.get(0).quantity());
        assertEquals("Test Product", completeProducts.get(0).name());
    }

    @Test
    void getCompleteProducts_shouldReturnEmptyListWhenBucketIsEmpty() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));
        when(bucketItemRepository.findAllByBucketId(1L)).thenReturn(Collections.emptyList());

        List<CompleteProduct> completeProducts = bucketService.getCompleteProducts(1L);

        assertNotNull(completeProducts);
        assertEquals(0, completeProducts.size());
    }

    @Test
    void restore_shouldRestoreBucketItemsWithBatch() {
        List<BucketItemDTO> items = List.of(new BucketItemDTO(1L, "item", 1, BigDecimal.ONE));
        OrderDTO orderDTO = new OrderDTO(UUID.randomUUID(), 1L, items, BigDecimal.ONE);
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));

        bucketService.restore(orderDTO);

        verify(bucketItemRepository, times(1)).batchInsert(bucket.id(), items);
    }

    @Test
    void addProduct_shouldIncreaseQuantityForExistingProduct() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));
        BucketItem existingItem = new BucketItem(1L, 1L, 1L, 1);
        when(bucketItemRepository.findByBucketIdAndProductId(bucket.id(), 1L)).thenReturn(Optional.of(existingItem));

        bucketService.addProduct(1L, 1L);

        verify(bucketItemRepository, times(1)).updateQuantity(bucket.id(), 1L, 2);
        verify(bucketItemRepository, never()).addProduct(anyLong(), anyLong(), anyInt());
    }

    @Test
    void addProduct_shouldAddNewProduct() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));
        when(bucketItemRepository.findByBucketIdAndProductId(bucket.id(), 1L)).thenReturn(Optional.empty());

        bucketService.addProduct(1L, 1L);

        verify(bucketItemRepository, never()).updateQuantity(anyLong(), anyLong(), (int) anyLong());
        verify(bucketItemRepository, times(1)).addProduct(bucket.id(), 1L, 1);
    }

    @Test
    void removeProductFromBucket_shouldDecreaseQuantity() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));
        BucketItem existingItem = new BucketItem(1L, 1L, 1L, 2);
        when(bucketItemRepository.findByBucketIdAndProductId(bucket.id(), 1L)).thenReturn(Optional.of(existingItem));

        bucketService.removeProductFromBucket(1L, 1L);

        verify(bucketItemRepository, never()).deleteProduct(bucket.id(), 1L);
        verify(bucketItemRepository, times(1)).updateQuantity(bucket.id(), 1L, 1);
    }

    @Test
    void removeProductFromBucket_shouldRemoveProductWhenQuantityIsOne() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));
        BucketItem existingItem = new BucketItem(1L, 1L, 1L, 1);
        when(bucketItemRepository.findByBucketIdAndProductId(bucket.id(), 1L)).thenReturn(Optional.of(existingItem));

        bucketService.removeProductFromBucket(1L, 1L);

        verify(bucketItemRepository, times(1)).deleteProduct(bucket.id(), 1L);
        verify(bucketItemRepository, never()).updateQuantity(anyLong(), anyLong(), (int) anyLong());
    }

    @Test
    void clearBucket_shouldClearAllItems() {
        when(bucketRepository.findBucketByClientId(1L)).thenReturn(Optional.of(bucket));

        bucketService.clearBucket(1L);

        verify(bucketItemRepository, times(1)).clearBucket(bucket.id());
    }
}
