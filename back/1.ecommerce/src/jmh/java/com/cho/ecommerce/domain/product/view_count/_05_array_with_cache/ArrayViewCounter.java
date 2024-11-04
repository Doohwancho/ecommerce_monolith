package com.cho.ecommerce.domain.product.view_count._05_array_with_cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/*
    Benchmark Results
    
    Benchmark                                                                                                   (threadCount)   Mode  Cnt      Score   Error   Units
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.redis_read                                               2  thrpt    2     78.454          ops/ms
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.redis_write                                              2  thrpt    2     83.123          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.max_heap_read                                                 2  thrpt    2    774.455          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.max_heap_write                                                2  thrpt    2     51.277          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.concurrentSkipList_read                                     2  thrpt    2  49656.661          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.concurrentSkipList_write                                    2  thrpt    2     36.697          ops/ms
    c.c.e.d.p.v._04_concurrentHashMap_with_cache.CachedViewCounterBenchmark.concurrentHashMap_with_cache_read               2  thrpt    2  15961.838          ops/ms
    c.c.e.d.p.v._04_concurrentHashMap_with_cache.CachedViewCounterBenchmark.concurrentHashMap_with_cache_write              2  thrpt    2  15699.702          ops/ms
    c.c.e.d.p.v._05_array_with_cache.ArrayViewCounterBenchmark.array_read                                                   2  thrpt    2  34748.974          ops/ms
    c.c.e.d.p.v._05_array_with_cache.ArrayViewCounterBenchmark.array_write                                                  2  thrpt    2  18876.308          ops/ms

 */

@Slf4j
public class ArrayViewCounter implements AutoCloseable {
    
    @Value
    private static class ProductView {
        
        int productId;
        long viewCount;
    }
    
    private final AtomicLongArray viewCounts;
    private final AtomicReference<List<ProductView>> cachedTopProducts;
    private final ScheduledExecutorService scheduler;
    private final int maxProductId;
    
    public ArrayViewCounter(int maxProductId) {
        this.maxProductId = maxProductId;
        this.viewCounts = new AtomicLongArray(maxProductId);
        this.cachedTopProducts = new AtomicReference<>(new ArrayList<>());
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "array-view-counter-cache-updater");
            thread.setDaemon(true);
            return thread;
        });
        
        // Schedule periodic cache updates
        scheduler.scheduleAtFixedRate(
            this::updateTopProductsCache,
            0,
            10,
            TimeUnit.MINUTES
        );
    }
    
    /**
     * Increment view count with optimistic locking O(1) operation
     */
    public boolean incrementView(int productId, long delta) {
        if (productId >= maxProductId) {
            log.warn("Product ID {} exceeds maximum {}", productId, maxProductId);
            return false;
        }
        
        while (true) {
            long currentCount = viewCounts.get(productId);
            if (viewCounts.compareAndSet(productId, currentCount, currentCount + delta)) {
                return true;
            }
            // If CAS fails, retry
        }
    }
    
    /**
     * Get top N products from cache O(1) operation since it returns cached result
     */
    public List<ProductView> getTopNProducts(int n) {
        List<ProductView> cached = cachedTopProducts.get();
        return new ArrayList<>(cached.subList(0, Math.min(n, cached.size())));
    }
    
    /**
     * Get current view count for a product O(1) operation
     */
    public long getViewCount(int productId) {
        if (productId >= maxProductId) {
            return 0;
        }
        return viewCounts.get(productId);
    }
    
    /**
     * Update cached top products using quick select algorithm O(n) average case for finding top k
     * elements
     */
    public synchronized void updateTopProductsCache() {
        try {
            int n = viewCounts.length();
            ProductView[] products = new ProductView[n];
            
            // Create array of product views
            for (int i = 0; i < n; i++) {
                long count = viewCounts.get(i);
                if (count > 0) { // Only include products with views
                    products[i] = new ProductView(i, count);
                }
            }
            
            // Filter out null entries and sort
            ProductView[] nonZeroProducts = Arrays.stream(products)
                .filter(Objects::nonNull)
                .toArray(ProductView[]::new);
            
            // Use quick sort for smaller arrays (< 10000) and merge sort for larger ones
            if (nonZeroProducts.length < 10000) {
                quickSort(nonZeroProducts, 0, nonZeroProducts.length - 1);
            } else {
                Arrays.sort(nonZeroProducts, (a, b) -> Long.compare(b.viewCount, a.viewCount));
            }
            
            // Update cache atomically
            cachedTopProducts.set(Arrays.asList(nonZeroProducts));
            
            log.debug("Updated top products cache with {} entries", nonZeroProducts.length);
        } catch (Exception e) {
            log.error("Failed to update top products cache", e);
        }
    }
    
    /**
     * Quick sort implementation optimized for smaller arrays O(n log n) average case
     */
    private void quickSort(ProductView[] arr, int low, int high) {
        if (low < high) {
            if (high - low < 10) {
                // Use insertion sort for very small subarrays
                insertionSort(arr, low, high);
            } else {
                int pi = partition(arr, low, high);
                quickSort(arr, low, pi - 1);
                quickSort(arr, pi + 1, high);
            }
        }
    }
    
    private void insertionSort(ProductView[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            ProductView key = arr[i];
            int j = i - 1;
            while (j >= low && arr[j].viewCount < key.viewCount) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
    
    private int partition(ProductView[] arr, int low, int high) {
        // Use median-of-three for pivot selection
        int mid = low + (high - low) / 2;
        long pivot = medianOfThree(
            arr[low].viewCount,
            arr[mid].viewCount,
            arr[high].viewCount
        );
        
        // Find pivot index
        int pivotIndex = arr[low].viewCount == pivot ? low :
            arr[mid].viewCount == pivot ? mid : high;
        
        // Swap pivot to end
        swap(arr, pivotIndex, high);
        pivot = arr[high].viewCount;
        
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j].viewCount > pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
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
    
    private void swap(ProductView[] arr, int i, int j) {
        ProductView temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
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