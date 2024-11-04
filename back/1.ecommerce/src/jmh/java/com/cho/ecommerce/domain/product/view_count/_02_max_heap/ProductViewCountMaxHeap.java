package com.cho.ecommerce.domain.product.view_count._02_max_heap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;

public class ProductViewCountMaxHeap {
    
    @Getter
    private static class MockProduct implements Comparable<MockProduct> {
        
        private final String id;
        private volatile long viewCount;
        private final ReadWriteLock lock;
        
        public MockProduct(String id, long initialViewCount) {
            this.id = id;
            this.viewCount = initialViewCount;
            this.lock = new ReentrantReadWriteLock();
        }
        
        public void incrementViews(long delta) {
            lock.writeLock().lock();
            try {
                this.viewCount += delta;
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public long getViewCount() {
            lock.readLock().lock();
            try {
                return viewCount;
            } finally {
                lock.readLock().unlock();
            }
        }
        
        @Override
        public int compareTo(MockProduct other) {
            // Note: Using long comparison to avoid integer overflow
            return Long.compare(other.viewCount, this.viewCount); // Descending order
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MockProduct mockProduct = (MockProduct) o;
            return id.equals(mockProduct.id);
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }
        
        @Override
        public String toString() {
            return String.format("Product{id='%s', views=%d}", id, viewCount);
        }
    }
    
    private final ConcurrentHashMap<String, MockProduct> productMap;
    private final PriorityQueue<MockProduct> maxHeap;
    private final ReadWriteLock heapLock;
    
    public ProductViewCountMaxHeap() {
        this.productMap = new ConcurrentHashMap<>();
        this.maxHeap = new PriorityQueue<>();
        this.heapLock = new ReentrantReadWriteLock();
    }
    
    /**
     * Adds a new product or updates view count of existing product Thread-safe method for
     * concurrent access
     */
    public void incrementView(String productId, long viewDelta) {
        MockProduct mockProduct = productMap.compute(productId, (id, existingMockProduct) -> {
            if (existingMockProduct == null) {
                return new MockProduct(id, viewDelta);
            }
            existingMockProduct.incrementViews(viewDelta);
            return existingMockProduct;
        });
        
        heapLock.writeLock().lock();
        try {
            // Remove and re-add to update position in heap
            maxHeap.remove(mockProduct);
            maxHeap.offer(mockProduct);
        } finally {
            heapLock.writeLock().unlock();
        }
    }
    
    /**
     * Returns top N products with highest view counts Thread-safe read operation
     */
    public List<MockProduct> getTopNProducts(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        
        heapLock.readLock().lock();
        try {
            List<MockProduct> result = new ArrayList<>();
            // Create a temporary heap for reading to avoid blocking writes
            PriorityQueue<MockProduct> tempHeap = new PriorityQueue<>(maxHeap);
            
            for (int i = 0; i < n && !tempHeap.isEmpty(); i++) {
                result.add(tempHeap.poll());
            }
            
            return result;
        } finally {
            heapLock.readLock().unlock();
        }
    }
    
    /**
     * Get the current view count for a product Thread-safe read operation
     */
    public long getViewCount(String productId) {
        MockProduct mockProduct = productMap.get(productId);
        return mockProduct != null ? mockProduct.getViewCount() : 0;
    }
    
    /**
     * Get total number of products in the heap Thread-safe read operation
     */
    public int size() {
        return productMap.size();
    }
    
    /**
     * Removes a product from the tracking system Thread-safe write operation
     */
    public void removeProduct(String productId) {
        MockProduct mockProduct = productMap.remove(productId);
        if (mockProduct != null) {
            heapLock.writeLock().lock();
            try {
                maxHeap.remove(mockProduct);
            } finally {
                heapLock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Clear all products Thread-safe write operation
     */
    public void clear() {
        heapLock.writeLock().lock();
        try {
            productMap.clear();
            maxHeap.clear();
        } finally {
            heapLock.writeLock().unlock();
        }
    }
}
