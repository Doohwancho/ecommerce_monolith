package com.cho.ecommerce.domain.product.view_count._07_primitive_array_with_cache_and_optimized_sort;


import com.cho.ecommerce.domain.product.view_count._06_primitive_array_with_cache.PrimitiveArrayViewCounter;
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

/*
Benchmark                                                                                (maxProductId)  (nonZeroElements)  (threadCount)    Mode     Cnt      Score    Error   Units
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000                 50              2   thrpt       2      9.831           ops/ms
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000              10000              2   thrpt       2      4.357           ops/ms
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000             100000              2   thrpt       2      0.316           ops/ms
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000                 50              2   thrpt       2      4.451           ops/ms
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000              10000              2   thrpt       2      1.928           ops/ms
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000             100000              2   thrpt       2      0.228           ops/ms
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000                 50              2    avgt       2      0.208            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000              10000              2    avgt       2      0.459            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000             100000              2    avgt       2      6.263            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000                 50              2    avgt       2      0.513            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000              10000              2    avgt       2   2605.045            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000             100000              2    avgt       2   1600.570            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000                 50              2  sample  200193      0.200 ±  0.001   ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.00            100000                 50              2  sample              0.193            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.50            100000                 50              2  sample              0.195            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.90            100000                 50              2  sample              0.216            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.95            100000                 50              2  sample              0.218            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.99            100000                 50              2  sample              0.226            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.999           100000                 50              2  sample              0.254            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.9999          100000                 50              2  sample              0.851            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p1.00            100000                 50              2  sample              5.562            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000              10000              2  sample   88290      0.453 ±  0.001   ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.00            100000              10000              2  sample              0.426            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.50            100000              10000              2  sample              0.443            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.90            100000              10000              2  sample              0.486            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.95            100000              10000              2  sample              0.494            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.99            100000              10000              2  sample              0.513            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.999           100000              10000              2  sample              0.795            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.9999          100000              10000              2  sample              1.710            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p1.00            100000              10000              2  sample             23.134            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000             100000              2  sample    6335      6.311 ±  0.021   ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.00            100000             100000              2  sample              5.775            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.50            100000             100000              2  sample              6.341            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.90            100000             100000              2  sample              6.742            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.95            100000             100000              2  sample              6.824            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.99            100000             100000              2  sample              7.318            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.999           100000             100000              2  sample             11.472            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p0.9999          100000             100000              2  sample             23.298            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort:optimizedUpdateCache·p1.00            100000             100000              2  sample             23.298            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000                 50              2  sample   90599      0.452 ±  0.355   ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.00                       100000                 50              2  sample              0.216            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.50                       100000                 50              2  sample              0.219            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.90                       100000                 50              2  sample              0.225            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.95                       100000                 50              2  sample              0.231            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.99                       100000                 50              2  sample              0.244            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.999                      100000                 50              2  sample              0.309            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.9999                     100000                 50              2  sample              1.965            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p1.00                       100000                 50              2  sample           5989.466            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000              10000              2  sample   39770      1.093 ±  1.106   ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.00                       100000              10000              2  sample              0.496            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.50                       100000              10000              2  sample              0.499            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.90                       100000              10000              2  sample              0.509            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.95                       100000              10000              2  sample              0.515            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.99                       100000              10000              2  sample              0.531            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.999                      100000              10000              2  sample              0.654            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.9999                     100000              10000              2  sample           2056.810            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p1.00                       100000              10000              2  sample          11576.279            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000             100000              2  sample    4937     10.320 ± 11.823   ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.00                       100000             100000              2  sample              4.022            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.50                       100000             100000              2  sample              4.039            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.90                       100000             100000              2  sample              4.080            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.95                       100000             100000              2  sample              4.100            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.99                       100000             100000              2  sample              4.224            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.999                      100000             100000              2  sample           1248.086            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p0.9999                     100000             100000              2  sample          15837.692            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort:simpleUpdateCache·p1.00                       100000             100000              2  sample          15837.692            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000                 50              2      ss       2      0.406            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000              10000              2      ss       2      0.541            ms/op
UpdateCacheStrategyBenchmark.optimized_multi_strategy_sort                                       100000             100000              2      ss       2      6.227            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000                 50              2      ss       2      0.422            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000              10000              2      ss       2      1.428            ms/op
UpdateCacheStrategyBenchmark.simple_insertion_sort                                               100000             100000              2      ss       2      6.080            ms/op
 */

@State(Scope.Benchmark)
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
public class UpdateCacheStrategyBenchmark {
    
    @Param({"100000"}) // Total array size
    private int maxProductId;
    
    @Param({"50", "10000", "100000"})
    private int nonZeroElements;
    
    @Param({"2"})
    private int threadCount;
    
    
    private PrimitiveArrayViewCounter simpleCounter;
    private PrimitiveArrayViewCounterSortOptimized optimizedCounter;
    
    @Setup
    public void setup() {
        simpleCounter = new PrimitiveArrayViewCounter(maxProductId);
        optimizedCounter = new PrimitiveArrayViewCounterSortOptimized(maxProductId);
        
        // Initialize with nonZeroElements number of products having views
        int viewsToAdd = Math.max(1,
            10000 / nonZeroElements); // Ensure we get meaningful view counts
        
        for (int i = 0; i < nonZeroElements && i < maxProductId; i++) {
            int views = viewsToAdd * (i + 1); // Different view counts for different products
            simpleCounter.incrementView(i, views);
            optimizedCounter.incrementView(i, views);
        }
    }
    
    @TearDown
    public void tearDown() {
        simpleCounter.close();
        simpleCounter = null;
        
        optimizedCounter.close();
        optimizedCounter = null;
    }

//    @Benchmark
//    @Group("simple_insertion_sort")
//    @GroupThreads(2)
//    public void simpleUpdateCache() {
//        simpleCounter.updateTopProductsCache();
//    }
    
    @Benchmark
    @Group("simple_insertion_sort")
    @GroupThreads(2)
    public void simpleUpdateCache() {
        simpleCounter.updateTopProductsCache();
    }
    
    @Benchmark
    @Group("optimized_multi_strategy_sort")
    @GroupThreads(2)
    public void optimizedUpdateCache() {
        optimizedCounter.updateTopProductsCache();
    }
    // Verify results are the same
//    @Benchmark
//    @Group("verify")
//    public void verifyResults(Blackhole blackhole) {
//        simpleCounter.updateTopProductsCache();
//        optimizedCounter.updateTopProductsCache();
//
//        int[] simpleTop10 = simpleCounter.getTopNProductIds(10);
//        int[] optimizedTop10 = optimizedCounter.getTopNProductIds(10);
//
//        // Verify counts match
//        for (int i = 0; i < 10; i++) {
//            blackhole.consume(
//                simpleCounter.getViewCount(simpleTop10[i]) ==
//                    optimizedCounter.getViewCount(optimizedTop10[i])
//            );
//        }
//    }
}