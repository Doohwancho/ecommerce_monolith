package com.cho.ecommerce.domain.product.view_count._04_concurrentHashMap_with_cache;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/*
    benchmark results
    
    Benchmark                                                                             (threadCount)   Mode  Cnt      Score   Error   Units
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.redis_read                         2  thrpt    2     78.281          ops/ms
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.redis_write                        2  thrpt    2     83.422          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.max_heap_read                           2  thrpt    2    772.524          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.max_heap_write                          2  thrpt    2     50.215          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.concurrentSkipList_read               2  thrpt    2  49411.056          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.concurrentSkipList_write              2  thrpt    2     37.060          ops/ms
    c.c.e.d.p.v._04_concurrentHashMap_with_cache.CachedViewCounterBenchmark.cached_read               2  thrpt    2  16019.388          ops/ms
    c.c.e.d.p.v._04_concurrentHashMap_with_cache.CachedViewCounterBenchmark.cached_write              2  thrpt    2  15893.130          ops/ms
 */

@Slf4j
public class CachedViewCounter implements AutoCloseable {
    
    private final ConcurrentHashMap<String, LongAdder> viewCounts;
    private volatile List<Map.Entry<String, Long>> cachedTopProducts;
    private final ScheduledExecutorService scheduler;
    private static final int DEFAULT_CACHE_UPDATE_MINUTES = 10;
    
    public CachedViewCounter() {
        this(DEFAULT_CACHE_UPDATE_MINUTES);
    }
    
    public CachedViewCounter(int cacheUpdateMinutes) {
        this.viewCounts = new ConcurrentHashMap<>();
        this.cachedTopProducts = new ArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "view-counter-cache-updater");
            thread.setDaemon(true);
            return thread;
        });
        
        // Schedule periodic cache updates
        scheduler.scheduleAtFixedRate(
            this::updateTopProductsCache,
            0,
            cacheUpdateMinutes,
            TimeUnit.MINUTES
        );
    }
    
    /**
     * Increment view count for a product Fast O(1) write operation
     */
    public void incrementView(String productId, long delta) {
        viewCounts.computeIfAbsent(productId, k -> new LongAdder())
            .add(delta);
    }
    
    /**
     * Get top N products from cache Fast O(1) read operation
     */
    public List<String> getTopNProducts(int n) {
        return cachedTopProducts.stream()
            .limit(n)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Get top N products with their view counts from cache Fast O(1) read operation
     */
    public List<Map.Entry<String, Long>> getTopNProductsWithCounts(int n) {
        return new ArrayList<>(cachedTopProducts.subList(0, Math.min(n, cachedTopProducts.size())));
    }
    
    /**
     * Get current view count for a product Fast O(1) read operation
     */
    public long getViewCount(String productId) {
        LongAdder counter = viewCounts.get(productId);
        return counter != null ? counter.sum() : 0;
    }
    
    /**
     * Force update of the top products cache Called periodically and can be called manually if
     * needed
     */
    public synchronized void updateTopProductsCache() {
        try {
            // Convert LongAdder values to actual counts and sort
            List<Map.Entry<String, Long>> sortedProducts = viewCounts.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().sum()))
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
            
            // Update the cached list atomically
            cachedTopProducts = Collections.unmodifiableList(sortedProducts);
            
            log.debug("Updated top products cache with {} entries", sortedProducts.size());
        } catch (Exception e) {
            log.error("Failed to update top products cache", e);
        }
    }
    
    /**
     * Get size of the view counter
     */
    public int size() {
        return viewCounts.size();
    }
    
    /**
     * Remove a product
     */
    public void removeProduct(String productId) {
        viewCounts.remove(productId);
    }
    
    /**
     * Clear all data
     */
    public void clear() {
        viewCounts.clear();
        cachedTopProducts = new ArrayList<>();
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
