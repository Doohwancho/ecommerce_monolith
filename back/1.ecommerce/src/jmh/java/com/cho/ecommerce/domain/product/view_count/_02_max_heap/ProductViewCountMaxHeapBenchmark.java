package com.cho.ecommerce.domain.product.view_count._02_max_heap;


import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
public class ProductViewCountMaxHeapBenchmark {
    
    private ProductViewCountMaxHeap heap;
    private static final int PRODUCT_COUNT = 10000;
    private static final int INITIAL_VIEWS = 10000;
    
    @Param({"2"})
    private int threadCount;
    
    @Setup
    public void setup() {
        heap = new ProductViewCountMaxHeap();
        
        // Initialize products with random view counts
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            String productId = "product-" + i;
            int viewCount = (int) (Math.random() * INITIAL_VIEWS);
            heap.incrementView(productId, viewCount);
        }
    }
    
    @TearDown
    public void tearDown() {
        heap.clear();
        heap = null;
    }
    
    @Benchmark
    @Group("max_heap_write")
    @GroupThreads(2)
    public void writeTest() {
        int randomProductIndex = (int) (Math.random() * PRODUCT_COUNT);
        String productId = "product-" + randomProductIndex;
        heap.incrementView(productId, 1);
    }
    
    @Benchmark
    @Group("max_heap_read")
    @GroupThreads(2)
    public void readTest(Blackhole blackhole) {
        blackhole.consume(heap.getTopNProducts(10));
    }

//    // Mixed read-write benchmark
//    @Benchmark
//    @Group("heap_mixed")
//    @GroupThreads(4)
//    public void mixedTest(Blackhole blackhole) {
//        if (Math.random() < 0.8) { // 80% reads, 20% writes
//            blackhole.consume(heap.getTopNProducts(10));
//        } else {
//            int randomProductIndex = (int) (Math.random() * PRODUCT_COUNT);
//            String productId = "product-" + randomProductIndex;
//            heap.incrementView(productId, 1);
//        }
//    }
    
    // Burst write benchmark
//    @Benchmark
//    @Group("heap_burst_write")
//    @GroupThreads(4)
//    public void burstWriteTest() {
//        // Simulate burst of 10 writes to randomly selected products
//        for (int i = 0; i < 10; i++) {
//            int randomProductIndex = (int) (Math.random() * PRODUCT_COUNT);
//            String productId = "product-" + randomProductIndex;
//            heap.incrementView(productId, 1);
//        }
//    }
    
    // Heavy read benchmark
//    @Benchmark
//    @Group("heap_heavy_read")
//    @GroupThreads(4)
//    public void heavyReadTest(Blackhole blackhole) {
//        // Multiple reads with different N values
//        blackhole.consume(heap.getTopNProducts(5));
//        blackhole.consume(heap.getTopNProducts(10));
//        blackhole.consume(heap.getTopNProducts(20));
//    }
    
    // Concurrent mixed workload benchmark
//    @Benchmark
//    @Group("heap_concurrent_mixed")
//    @GroupThreads(4)
//    public void concurrentMixedTest(Blackhole blackhole) {
//        if (Math.random() < 0.5) { // 50% reads, 50% writes
//            for (int i = 0; i < 5; i++) { // Multiple operations per iteration
//                if (Math.random() < 0.8) { // 80% reads within read operations
//                    blackhole.consume(heap.getTopNProducts((int) (Math.random() * 20) + 1));
//                } else {
//                    int randomProductIndex = (int) (Math.random() * PRODUCT_COUNT);
//                    String productId = "product-" + randomProductIndex;
//                    heap.incrementView(productId, 1);
//                }
//            }
//        } else {
//            // Burst write scenario
//            for (int i = 0; i < 10; i++) {
//                int randomProductIndex = (int) (Math.random() * PRODUCT_COUNT);
//                String productId = "product-" + randomProductIndex;
//                heap.incrementView(productId, 1);
//            }
//        }
//    }
}
