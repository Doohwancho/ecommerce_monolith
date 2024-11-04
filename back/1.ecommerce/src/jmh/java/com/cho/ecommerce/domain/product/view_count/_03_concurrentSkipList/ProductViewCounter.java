package com.cho.ecommerce.domain.product.view_count._03_concurrentSkipList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.LongAdder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/*
    Q. why its better than 02.max_heap ver?
    A. max_heap.ver writes twice (one for view_count, other one for ranking)
       ConcurrentSkipListMap is like TreeMap with concurrency-control.
       you write "once", and it orders items automatically.
       
    Q. benchmark results
    
    Benchmark                                                                         (threadCount)   Mode  Cnt      Score   Error   Units
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.first_redis_read               2  thrpt    2     77.903          ops/ms
    c.c.e.d.p.v._01_redis.benchmark.ProductViewCountRedisBenchmark.first_redis_write              2  thrpt    2     78.658          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.second_heap_read                    2  thrpt    2    741.954          ops/ms
    c.c.e.d.p.v._02_max_heap.ProductViewCountMaxHeapBenchmark.second_heap_write                   2  thrpt    2     49.924          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.third_counter_read                2  thrpt    2  49621.789          ops/ms
    c.c.e.d.p.v._03_concurrentSkipList.ViewCounterBenchmarkTest.third_counter_write               2  thrpt    2     37.911          ops/ms
 */

@Slf4j
public class ProductViewCounter {
    
    @Value
    private static class ViewCount implements Comparable<ViewCount> {
        
        String productId;
        long count;
        long timestamp; // For breaking ties in ordering
        
        @Override
        public int compareTo(ViewCount other) {
            int countCompare = Long.compare(other.count, this.count); // Descending by count
            if (countCompare != 0) {
                return countCompare;
            }
            return Long.compare(other.timestamp, this.timestamp); // Break ties with timestamp
        }
    }
    
    // Single source of truth: stores ViewCount objects ordered by count (descending)
    private final ConcurrentSkipListMap<ViewCount, LongAdder> viewCounts;
    
    public ProductViewCounter() {
        this.viewCounts = new ConcurrentSkipListMap<>();
    }
    
    /**
     * Increment view count for a product Thread-safe and performs single atomic update
     */
    public void incrementView(String productId, long delta) {
        while (true) { // CAS loop for atomic update
            Map.Entry<ViewCount, LongAdder> existingEntry = null;
            
            // Find existing entry for this productId
            for (Map.Entry<ViewCount, LongAdder> entry : viewCounts.entrySet()) {
                if (entry.getKey().productId.equals(productId)) {
                    existingEntry = entry;
                    break;
                }
            }
            
            if (existingEntry == null) {
                // New product - try to insert
                ViewCount newCount = new ViewCount(productId, delta, System.nanoTime());
                LongAdder counter = new LongAdder();
                counter.add(delta);
                
                if (viewCounts.putIfAbsent(newCount, counter) == null) {
                    // Successfully inserted
                    break;
                }
                // If insert failed, retry
                continue;
            }
            
            // Existing product - update count
            ViewCount oldCount = existingEntry.getKey();
            LongAdder counter = existingEntry.getValue();
            counter.add(delta);
            
            // Remove old entry and insert new one with updated count
            ViewCount newCount = new ViewCount(productId, oldCount.count + delta,
                oldCount.timestamp);
            if (viewCounts.remove(oldCount) != null &&
                viewCounts.putIfAbsent(newCount, counter) == null) {
                // Successfully updated
                break;
            }
            // If update failed, retry
        }
    }
    
    /**
     * Get top N products by view count Thread-safe read operation
     */
    public List<String> getTopNProducts(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        
        List<String> result = new ArrayList<>(n);
        int count = 0;
        
        for (ViewCount viewCount : viewCounts.keySet()) {
            if (count >= n) {
                break;
            }
            result.add(viewCount.productId);
            count++;
        }
        
        return result;
    }
    
    /**
     * Get current view count for a product Thread-safe read operation
     */
    public long getViewCount(String productId) {
        for (Map.Entry<ViewCount, LongAdder> entry : viewCounts.entrySet()) {
            if (entry.getKey().productId.equals(productId)) {
                return entry.getValue().sum();
            }
        }
        return 0;
    }
    
    /**
     * Get total number of products being tracked
     */
    public int size() {
        return viewCounts.size();
    }
    
    /**
     * Remove a product from tracking
     */
    public void removeProduct(String productId) {
        viewCounts.entrySet().removeIf(entry -> entry.getKey().productId.equals(productId));
    }
    
    /**
     * Clear all products
     */
    public void clear() {
        viewCounts.clear();
    }
    
    /**
     * Get top N products with their view counts Returns list of [productId, viewCount] pairs
     */
    public List<Object[]> getTopNProductsWithCounts(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        
        List<Object[]> result = new ArrayList<>(n);
        int count = 0;
        
        for (Map.Entry<ViewCount, LongAdder> entry : viewCounts.entrySet()) {
            if (count >= n) {
                break;
            }
            result.add(new Object[]{
                entry.getKey().productId,
                entry.getValue().sum()
            });
            count++;
        }
        
        return result;
    }
}