package com.cho.ecommerce.domain.product.view_count._05_array_with_cache;

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
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
//@Threads(4)
public class ArrayViewCounterBenchmark {
    
    private ArrayViewCounter viewCounter;
    private static final int MAX_PRODUCT_ID = 10_000;
    private static final int INITIAL_VIEWS = 10_000;
    
    @Param({"2"})
    private int threadCount;
    
    @Setup
    public void setup() {
        viewCounter = new ArrayViewCounter(MAX_PRODUCT_ID);
        
        // Initialize with test data
        for (int i = 0; i < MAX_PRODUCT_ID; i++) {
            int views = (int) (Math.random() * INITIAL_VIEWS);
            viewCounter.incrementView(i, views);
        }
        
        // Initial cache update
        viewCounter.updateTopProductsCache();
    }
    
    @TearDown
    public void tearDown() {
        viewCounter.close();
        viewCounter = null;
    }
    
    // Write benchmark
    @Benchmark
    @Group("array_write")
    @GroupThreads(2)
    public void writeTest() {
        int randomProduct = (int) (Math.random() * MAX_PRODUCT_ID);
        viewCounter.incrementView(randomProduct, 1);
    }
    
    // Read benchmark (from cache)
    @Benchmark
    @Group("array_read")
    @GroupThreads(2)
    public void readTest(Blackhole blackhole) {
        blackhole.consume(viewCounter.getTopNProducts(10));
    }
    
    // Mixed workload
//    @Benchmark
//    @Group("array_mixed")
//    @GroupThreads(4)
//    public void mixedTest(Blackhole blackhole) {
//        if (Math.random() < 0.8) { // 80% reads
//            blackhole.consume(viewCounter.getTopNProducts(10));
//        } else {
//            int randomProduct = (int) (Math.random() * MAX_PRODUCT_ID);
//            viewCounter.incrementView(randomProduct, 1);
//        }
//    }
    
    // Cache update benchmark
//    @Benchmark
//    @Group("array_cache_update")
//    @GroupThreads(1)
//    public void cacheUpdateTest() {
//        viewCounter.updateTopProductsCache();
//    }
    
    // Burst writes
//    @Benchmark
//    @Group("array_burst_write")
//    @GroupThreads(4)
//    public void burstWriteTest() {
//        for (int i = 0; i < 100; i++) {
//            int randomProduct = (int) (Math.random() * MAX_PRODUCT_ID);
//            viewCounter.incrementView(randomProduct, 1);
//        }
//    }
}
