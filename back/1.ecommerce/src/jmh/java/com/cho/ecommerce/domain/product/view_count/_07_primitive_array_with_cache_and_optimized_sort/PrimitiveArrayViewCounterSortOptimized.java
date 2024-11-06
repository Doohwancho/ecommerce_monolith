package com.cho.ecommerce.domain.product.view_count._07_primitive_array_with_cache_and_optimized_sort;


import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrimitiveArrayViewCounterSortOptimized implements AutoCloseable {
    
    private final int MAX_PRODUCT_ID;
    private static final int MAX_CACHED_SIZE = 100;
    // Thresholds for different sorting algorithms
    private static final int INSERTION_SORT_THRESHOLD = 50;    // Very small arrays
    private static final int QUICKSORT_THRESHOLD = 10_000;     // Medium arrays
    
    private final AtomicLongArray viewCounts;
    private final AtomicReference<int[]> cachedTopProductIds; // Store only IDs of top products
    private final ScheduledExecutorService scheduler;
    
    
    public PrimitiveArrayViewCounterSortOptimized(int MAX_PRODUCT_ID) {
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
    
    public void updateTopProductsCache() {
        try {
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
            
        } catch (Exception e) {
            log.error("Failed to update top products cache", e);
            //TODO - retry method
        }
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
        PriorityQueue<long[]> maxHeap = new PriorityQueue<>(
            MAX_CACHED_SIZE, // Only allocates space for 100 elements
            (a, b) -> Long.compare(b[1], a[1])  // Compare pre-fetched values
        );
        
        // First pass: fill heap with first MAX_CACHED_SIZE non-zero elements
        for (int i = 0; i < viewCounts.length(); i++) {
            long count = viewCounts.get(i);
            if (count > 0) {
                maxHeap.offer(new long[]{i, count});  // Store both id and count
                if (maxHeap.size() > MAX_CACHED_SIZE) {
                    maxHeap.poll(); //remove product with smallest view_count
                }
            }
        }
        
        // Convert heap to sorted array
        int[] result = new int[maxHeap.size()];
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