package com.cho.ecommerce.domain.product.view_count._06_primitive_array_with_cache;


import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

/*
    Benchmark                                                                                                   (threadCount)   Mode  Cnt       Score   Error   Units
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.redis_read                                               2  thrpt    2      81.402          ops/ms
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.redis_write                                              2  thrpt    2      83.705          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.max_heap_read                                                 2  thrpt    2     778.630          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.max_heap_write                                                2  thrpt    2      50.298          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.concurrentSkipList_read                                     2  thrpt    2   49278.453          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.concurrentSkipList_write                                    2  thrpt    2      35.381          ops/ms
    c.c.e.d.p.v._04_concurrentHashMap_with_cache.CachedViewCounterBenchmark.concurrentHashMap_with_cache_read               2  thrpt    2   15962.344          ops/ms
    c.c.e.d.p.v._04_concurrentHashMap_with_cache.CachedViewCounterBenchmark.concurrentHashMap_with_cache_write              2  thrpt    2   15855.741          ops/ms
    c.c.e.d.p.v._05_array_with_cache.ArrayViewCounterBenchmark.array_read                                                   2  thrpt    2   48065.797          ops/ms
    c.c.e.d.p.v._05_array_with_cache.ArrayViewCounterBenchmark.array_write                                                  2  thrpt    2   18921.207          ops/ms
    c.c.e.d.p.v._06_optimized_array_with_cache.ArrayViewCounterOptimizedBenchmark.array_optimized_read                      2  thrpt    2  269472.295          ops/ms
    c.c.e.d.p.v._06_optimized_array_with_cache.ArrayViewCounterOptimizedBenchmark.array_optimized_write                     2  thrpt    2   19227.155          ops/ms
 */

@Slf4j
public class PrimitiveArrayViewCounter implements AutoCloseable {
    
    private final int MAX_PRODUCT_ID;
    private static final int MAX_CACHED_SIZE = 100;
    
    private final AtomicLongArray viewCounts;
    private final AtomicReference<int[]> cachedTopProductIds; // Store only IDs of top products
    private final ScheduledExecutorService scheduler;
    
    public PrimitiveArrayViewCounter(int MAX_PRODUCT_ID) {
        this.MAX_PRODUCT_ID = MAX_PRODUCT_ID;
        this.viewCounts = new AtomicLongArray(MAX_PRODUCT_ID);
        this.cachedTopProductIds = new AtomicReference<>(
            new int[MAX_CACHED_SIZE]); //pre-allocate memory
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "array-view-counter-cache-updater");
            thread.setDaemon(true);
            return thread;
        });
        
        scheduler.scheduleAtFixedRate(
            this::updateTopProductsCache,
            0,
            10,
            TimeUnit.MINUTES
        );
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
    
    public int[] getTopNProductIds(int n) {
        if (n > MAX_CACHED_SIZE) {
            throw new IllegalArgumentException(
                String.format("Please request less than %d products. Current request: %d",
                    MAX_CACHED_SIZE, n));
        }
        int[] cached = cachedTopProductIds.get();
        return Arrays.copyOf(cached, Math.min(n, cached.length));
    }
    
    public long getViewCount(int productId) {
        if (productId >= MAX_PRODUCT_ID) {
            return 0;
        }
        return viewCounts.get(productId);
    }
    
    public synchronized void updateTopProductsCache() {
        try {
            // Find indices of top MAX_CACHED_SIZE values
            int[] topIndices = new int[MAX_CACHED_SIZE];
            long[] topValues = new long[MAX_CACHED_SIZE];
            int filledSize = 0;
            long minTopValue = 0;
            
            // First pass: find first MAX_CACHED_SIZE non-zero values
            for (int i = 0; i < viewCounts.length() && filledSize < MAX_CACHED_SIZE; i++) {
                long count = viewCounts.get(i);
                if (count > 0) {
                    insertSorted(topIndices, topValues, i, count, filledSize++);
                    if (filledSize == MAX_CACHED_SIZE) {
                        minTopValue = topValues[MAX_CACHED_SIZE - 1];
                    }
                }
            }
            
            // Second pass: update if we find larger values
            for (int i = filledSize; i < viewCounts.length(); i++) {
                long count = viewCounts.get(i);
                if (count > minTopValue) {
                    insertSorted(topIndices, topValues, i, count, MAX_CACHED_SIZE);
                    minTopValue = topValues[MAX_CACHED_SIZE - 1];
                }
            }
            
            // Update cache with just the product IDs
            cachedTopProductIds.set(Arrays.copyOf(topIndices, filledSize));
            
            log.debug("Updated top {} products cache", filledSize);
        } catch (Exception e) {
            log.error("Failed to update top products cache", e);
        }
    }
    
    private void insertSorted(int[] indices, long[] values, int newIndex, long newValue, int size) {
        int pos = size - 1;
        // Shift elements right while the new value is larger
        while (pos >= 0 && values[pos] < newValue) {
            if (pos + 1 < values.length) {
                values[pos + 1] = values[pos];
                indices[pos + 1] = indices[pos];
            }
            pos--;
        }
        // Insert the new value
        pos++;
        if (pos < size) {
            values[pos] = newValue;
            indices[pos] = newIndex;
        }
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