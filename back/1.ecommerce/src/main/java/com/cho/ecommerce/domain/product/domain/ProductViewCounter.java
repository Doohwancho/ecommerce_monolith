package com.cho.ecommerce.domain.product.domain;

import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductViewCounter implements AutoCloseable {
    
    private final int MAX_PRODUCT_ID;
    private static final int MAX_CACHED_SIZE = 100;
    // Thresholds for different sorting algorithms
    private static final int INSERTION_SORT_THRESHOLD = 50;    // Very small arrays
    private static final int QUICKSORT_THRESHOLD = 10_000;     // Medium arrays
    
    private final AtomicLongArray viewCounts;
    private final AtomicReference<int[]> cachedTopProductIds; // Store only IDs of top products
    private final AtomicReference<List<com.cho.ecommerce.api.domain.ProductDTO>> cachedTopProductDTOs;
    private final ScheduledExecutorService scheduler;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    
    public ProductViewCounter(@Value("${app.product.max-id:10000}") int MAX_PRODUCT_ID,
        ProductRepository productRepository,
        ProductMapper productMapper) {
        this.MAX_PRODUCT_ID = MAX_PRODUCT_ID;
        this.viewCounts = new AtomicLongArray(MAX_PRODUCT_ID);
        this.cachedTopProductDTOs = new AtomicReference<>(new ArrayList<>());
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.cachedTopProductIds = new AtomicReference<>(
            new int[MAX_CACHED_SIZE]); //pre-allocate memory
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "array-view-counter-cache-updater");
            thread.setDaemon(true);
            return thread;
        });
        
        scheduler.scheduleAtFixedRate(
            this::updateTopProductsCache,
            1, //initialize할 때, productId 0~9까지 1씩 임의로 주니까, 1분 후에 다시 카운트 시작
            10,
            TimeUnit.MINUTES
        );
    }
    
    public void initialize() {
        log.info("Initializing ProductViewCounter with default view counts...");
        
        // Set initial view counts for products 0-9
        for (int i = 0; i < 10; i++) {
            viewCounts.set(i, 1L);
        }
        
        // Update cache immediately instead of waiting for first scheduled update
        try {
            updateTopProductsCache();
            log.info("ProductViewCounter initialized with {} cached products",
                cachedTopProductDTOs.get().size());
        } catch (Exception e) {
            log.error("Failed to initialize ProductViewCounter cache", e);
        }
    }
    
    public boolean incrementView(int productId, long delta) {
        if (productId >= MAX_PRODUCT_ID) {
            log.warn("Product ID {} exceeds maximum {}", productId, MAX_PRODUCT_ID);
            return false;
        }
        
        while (true) {
            long currentCount = viewCounts.get(productId);
            if (viewCounts.compareAndSet(productId, currentCount, currentCount + delta)) {
                return true;
            }
        }
    }
    
    public List<Long> getTopNProductIds(int n) {
        if (n > MAX_CACHED_SIZE) {
            throw new IllegalArgumentException(
                String.format("Please request less than %d products. Current request: %d",
                    MAX_CACHED_SIZE, n));
        }
        int[] cached = cachedTopProductIds.get();
        return Arrays.stream(cached)
            .limit(Math.min(n, cached.length))
            .mapToObj(Long::valueOf)
            .collect(Collectors.toList());
    }
    
    public long getViewCount(int productId) {
        if (productId >= MAX_PRODUCT_ID) {
            return 0;
        }
        return viewCounts.get(productId);
    }
    
    private void resetProductViewCounts() {
        for (int i = 0; i < MAX_PRODUCT_ID; i++) {
            viewCounts.set(i, 0);
        }
        log.info("Reset period view counts");
    }
    
    public List<ProductDTO> getTopViewedProductsCached() {
        return cachedTopProductDTOs.get();
    }
    
    public void updateTopProductsCache() {
        try {
            // 1. Update the top product IDs
            updateTopProductsCacheInternal();
            
            // 2. get top 10 viewed products Ids
            List<Long> topProductIds = getTopNProductIds(10);
            
            if (!topProductIds.isEmpty()) {
                // 3. query products by their ids
                List<ProductEntity> topProducts = productRepository.findProductsByIdIn(
                    topProductIds);
                
                // 4. change ProductEntity type to ProductDTO type
                List<com.cho.ecommerce.api.domain.ProductDTO> topProductDTOList = productMapper.productEntitiesToProductDTOs(
                    topProducts);
                
                // 5. Cache the products
                cachedTopProductDTOs.set(topProductDTOList);
//                log.debug("Updated top products cache with {} products", topProducts.size());
            }
        } catch (Exception e) {
            log.error("Failed to update top products cache", e);
            //TODO - retry method
        }
    }
    
    public void updateTopProductsCacheInternal() {
        int nonZeroCount = countNonZeroElements();
        
        if (nonZeroCount == 0) {
            cachedTopProductIds.set(new int[0]);
            return;
        }
        if (nonZeroCount <= INSERTION_SORT_THRESHOLD) {
            updateWithInsertionSort(nonZeroCount);
        } else if (nonZeroCount <= QUICKSORT_THRESHOLD) {
            updateWithQuickSort(nonZeroCount);
        } else {
            updateWithHeapSelection();
        }
        
        resetProductViewCounts();
    }
    
    
    /**************************************************
     * Insertion Sort
     * For small arrays (n ≤ 50): Insertion Sort
     * Time: O(n²), Space: O(1), but very fast for small n due to simplicity
     */
    private void updateWithInsertionSort(int expectedSize) {
        int[] indices = new int[Math.min(MAX_CACHED_SIZE, expectedSize)];
        long[] values = new long[Math.min(MAX_CACHED_SIZE, expectedSize)];
        int size = 0;
        
        for (int i = 0; i < viewCounts.length() && size < MAX_CACHED_SIZE; i++) {
            long count = viewCounts.get(i);
            if (count > 0) {
                insertSorted(indices, values, i, count, size++);
            }
        }
        
        cachedTopProductIds.set(Arrays.copyOf(indices, size));
    }
    
    private void insertSorted(int[] indices, long[] values, int newIndex, long newValue,
        int currentSize) {
        int i = currentSize - 1;
        // Move elements that are smaller than newValue
        while (i >= 0 && values[i] < newValue) {
            if (i + 1 < values.length) {
                values[i + 1] = values[i];
                indices[i + 1] = indices[i];
            }
            i--;
        }
        // Insert the new value
        i++;
        if (i < values.length) {
            values[i] = newValue;
            indices[i] = newIndex;
        }
    }
    
    /**************************************************
     * Quick Sort
     * For medium arrays (50 < n ≤ 10,000): QuickSort
     * Time: O(n log n) average, Space: O(log n) for recursion
     */
    private void updateWithQuickSort(int expectedSize) {
        // Pre-allocate arrays based on expected size
        int[] indices = new int[expectedSize]; //index(product_id)를 넣는다.
        long[] values = new long[expectedSize]; //view_count를 넣는다
        int size = 0;
        
        // Collect non-zero elements
        for (int i = 0; i < viewCounts.length(); i++) {
            long count = viewCounts.get(i);
            if (count > 0) {
                indices[size] = i;
                values[size] = count;
                size++;
            }
        }
        
        // Sort collected elements
        quickSort(indices, values, 0,
            size - 1); //product_id가 든 array와 view_count가 든 array를 sort()한다.
        
        // Take top MAX_CACHED_SIZE elements
        cachedTopProductIds.set(Arrays.copyOf(indices, Math.min(MAX_CACHED_SIZE, size)));
    }
    
    private void quickSort(int[] indices, long[] values, int low, int high) {
        if (high - low <= 10) {
            // Use insertion sort for very small subarrays
            insertionSortRange(indices, values, low, high);
            return;
        }
        if (low < high) {
            int pi = partition(indices, values, low, high);
            quickSort(indices, values, low, pi - 1);
            quickSort(indices, values, pi + 1, high);
        }
    }
    
    private void insertionSortRange(int[] indices, long[] values, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            long currentValue = values[i];
            int currentIndex = indices[i];
            int j = i - 1;
            while (j >= low && values[j] < currentValue) {
                values[j + 1] = values[j];
                indices[j + 1] = indices[j];
                j--;
            }
            values[j + 1] = currentValue;
            indices[j + 1] = currentIndex;
        }
    }
    
    private int partition(int[] indices, long[] values, int low, int high) {
        int mid = low + (high - low) / 2;
        long pivot = medianOfThree(values[low], values[mid], values[high]);
        
        // Move pivot to end
        int pivotIndex = values[low] == pivot ? low :
            values[mid] == pivot ? mid : high;
        swap(indices, values, pivotIndex, high);
        
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (values[j] > values[high]) {
                i++;
                swap(indices, values, i, j);
            }
        }
        swap(indices, values, i + 1, high);
        return i + 1;
    }
    
    private long medianOfThree(long a, long b, long c) {
        if (a > b) {
            if (b > c) {
                return b;
            }
            if (a > c) {
                return c;
            }
            return a;
        }
        if (a > c) {
            return a;
        }
        if (b > c) {
            return c;
        }
        return b;
    }
    
    
    private void swap(int[] indices, long[] values, int i, int j) {
        int tempIndex = indices[i];
        indices[i] = indices[j];
        indices[j] = tempIndex;
        
        long tempValue = values[i];
        values[i] = values[j];
        values[j] = tempValue;
    }
    
    
    /**************************************************
     * Heap Selection Sort
     * For large arrays (n > 10,000): Heap Selection
     * Time: O(n + k log n), Space: O(k) where k = MAX_CACHED_SIZE
     */
    private void updateWithHeapSelection() {
        //step1) 힙 생성 (size = 100)
        PriorityQueue<long[]> maxHeap = new PriorityQueue<>(
            MAX_CACHED_SIZE, // Only allocates space for 100 elements
            (a, b) -> Long.compare(b[1], a[1])  // Compare pre-fetched values
        );
        
        //step2) N개 요소를 순회하면서
        for (int i = 0; i < viewCounts.length(); i++) { //O(N)
            long count = viewCounts.get(i);
            if (count > 0) { //get non-zero-view-count products only
                maxHeap.offer(new long[]{i, count});  // Store both id and count, O(log K), K = 100
                if (maxHeap.size() > MAX_CACHED_SIZE) {
                    maxHeap.poll(); //remove product with smallest view_count, O(log K)
                }
            }
        }
        
        // step3) Convert heap to sorted array
        int[] result = new int[maxHeap.size()]; //O(K log K), K = 100
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = (int) maxHeap.poll()[0];
        }
        
        cachedTopProductIds.set(result);
    }
    
    /**************************************
     * Util methods
     */
    private int countNonZeroElements() {
        int count = 0;
        for (int i = 0; i < viewCounts.length(); i++) {
            if (viewCounts.get(i) > 0) {
                count++;
            }
        }
        return count;
    }
    
    
    @Override
    public void close() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
