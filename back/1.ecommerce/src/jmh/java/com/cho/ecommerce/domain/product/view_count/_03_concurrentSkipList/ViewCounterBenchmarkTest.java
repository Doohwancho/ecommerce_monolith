package com.cho.ecommerce.domain.product.view_count._03_concurrentSkipList;

import java.util.List;
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
//@Threads(4) // Total number of threads for the benchmark
public class ViewCounterBenchmarkTest {
    
    private ProductViewCounter viewCounter;
    private static final int PRODUCT_COUNT = 10_000;
    private static final int INITIAL_VIEWS = 10_000;
    
    @Param({"2"}) // Test with different thread counts
    private int threadCount;
    
    @Setup
    public void setup() {
        viewCounter = new ProductViewCounter();
        
        // Initialize with test data
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            String productId = "product-" + i;
            int views = (int) (Math.random() * INITIAL_VIEWS);
            viewCounter.incrementView(productId, views);
        }
    }
    
    @TearDown
    public void tearDown() {
        viewCounter.clear();
        viewCounter = null;
    }
    
    @Benchmark
    @Group("third_counter_write")
    @GroupThreads(2)
    public void writeTest() {
        int randomProduct = (int) (Math.random() * PRODUCT_COUNT);
        viewCounter.incrementView("product-" + randomProduct, 1);
    }
    
    @Benchmark
    @Group("third_counter_read")
    @GroupThreads(2)
    public void readTest(Blackhole blackhole) {
        List<String> topProducts = viewCounter.getTopNProducts(10);
        blackhole.consume(topProducts);
    }
    
    // Read with counts benchmark
//    @Benchmark
//    @Group("counter_read_with_counts")
//    @GroupThreads(2)
//    public void readWithCountsTest(Blackhole blackhole) {
//        List<Object[]> topProductsWithCounts = viewCounter.getTopNProductsWithCounts(10);
//        blackhole.consume(topProductsWithCounts);
//    }
    
    // Mixed read/write benchmark with realistic ratio
//    @Benchmark
//    @Group("counter_mixed")
//    @GroupThreads(4)
//    public void mixedTest(Blackhole blackhole) {
//        double random = Math.random();
//        if (random < 0.8) { // 80% reads
//            List<String> topProducts = viewCounter.getTopNProducts(10);
//            blackhole.consume(topProducts);
//        } else { // 20% writes
//            int randomProduct = (int) (Math.random() * PRODUCT_COUNT);
//            viewCounter.incrementView("product-" + randomProduct, 1);
//        }
//    }
    
    // Burst write benchmark
//    @Benchmark
//    @Group("counter_burst_write")
//    @GroupThreads(4)
//    public void burstWriteTest() {
//        // Simulate burst of 10 writes
//        for (int i = 0; i < 10; i++) {
//            int randomProduct = (int) (Math.random() * PRODUCT_COUNT);
//            viewCounter.incrementView("product-" + randomProduct, 1);
//        }
//    }
    
    // Heavy read benchmark
//    @Benchmark
//    @Group("counter_heavy_read")
//    @GroupThreads(4)
//    public void heavyReadTest(Blackhole blackhole) {
//        // Multiple reads with different N values
//        blackhole.consume(viewCounter.getTopNProducts(5));
//        blackhole.consume(viewCounter.getTopNProducts(10));
//        blackhole.consume(viewCounter.getTopNProducts(20));
//    }
    
    // Single product repeated update benchmark
//    @Benchmark
//    @Group("counter_hot_product")
//    @GroupThreads(4)
//    public void hotProductTest() {
//        // Simulate hot product getting many views
//        viewCounter.incrementView("hot-product", 1);
//    }
}