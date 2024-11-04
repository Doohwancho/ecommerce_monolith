package com.cho.ecommerce.domain.product.view_count._01_redis.benchmark;


import com.cho.ecommerce.domain.product.view_count._01_redis.config.TestRedisConfig;
import com.cho.ecommerce.domain.product.view_count._01_redis.domain.MockProduct;
import com.cho.ecommerce.domain.product.view_count._01_redis.service.ProductRankingService;
import java.util.ArrayList;
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
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/*
    A. 테스트 환경
    - 8core 16GiB RAM pc로 테스트
    - 초기 jvm 힙사이즈 1G, 최대 힙 사이즈 2.5G, 메타스페이즈 256m
    - write(), read() 각각 2 쓰레드 할당
    
    B. 테스트 결과
    Benchmark                            (threadCount)   Mode  Cnt   Score    Error   Units
    ProductRankingBenchmark.redis_read               2  thrpt    3  75.602 ± 22.634  ops/ms
    ProductRankingBenchmark.redis_write              2  thrpt    3  83.334 ± 26.838  ops/ms

    평균 read()는 1초에 75602번 일어난다.
    평균 write()는 1초에 83334번 일어난다.
    
    C. 고려사항
    - 로컬 redis로 돌린거라 실전이었다면 WAS서버와 redis 사이에 network travel latency, 에러처리에 걸리는 시간 등이 고려되지 않았다.
    - 실전 redis의 스펙은 1000RPS까지 보통 2core 6GiB RAM 쓴다고 알려져 있는데, 로컬 pc가 8core 16GiB니까, 더 좋은 redis 스펙으로 테스트 된 것이다.
    - 실전에는 다른 많은 처리들과 함께 일어나기 때문에, 저 성능만큼 나오지는 않는다.
 */

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
@Threads(4) //총 4개의 스레드만 할당한다.
//total number of threads allocated for this benchmark test. 이걸 @GroupTrheads(8)로 메서드별로 나눠가져서 테스트 한다.
//Q. whats number of threads of ec2 with 2core, 4GiB RAM?
public class ProductViewCountRedisBenchmark {
    
    @Param({"2"}) // Test with different core counts
    private int threadCount;
    
    private ProductRankingService rankingService;
    private List<MockProduct> testProducts;
    private static final int PRODUCT_COUNT = 10000; // 100k products
    private static final int INITIAL_VIEWS = 10000; // Initial views per product
    private AnnotationConfigApplicationContext context;
    
    @Setup
    public void setup() {
        // Initialize minimal context with just the required beans
        context = new AnnotationConfigApplicationContext();
        context.register(TestRedisConfig.class);
        context.refresh();
        
        // Get the service directly
        rankingService = context.getBean(ProductRankingService.class);
        
        // Reset any existing data
        rankingService.resetAllRankings();
        
        // Create test products
        testProducts = new ArrayList<>();
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            MockProduct product = new MockProduct((long) i, "Product-" + i);
            testProducts.add(product);
        }
        
        // Initialize with random view counts
        for (MockProduct product : testProducts) {
            int viewCount = (int) (Math.random() * INITIAL_VIEWS);
            rankingService.incrementView(product, viewCount);
        }
    }
    
    @TearDown
    public void tearDown() {
        if (rankingService != null) {
            rankingService.resetAllRankings();
        }
        if (context != null) {
            context.close();
        }
        testProducts = null; // Help GC
    }
    
    // Simulates high-frequency write operations (product views)
    @Benchmark
//    @Threads(2) //writeTest() benchmark will be executed using 2 threads concurrently.
    @Group("first_redis_write")
    @GroupThreads(2)
    //It specifies how many threads should be allocated to a particular method within a group of benchmarks.
    //It's used to simulate different ratios of operations in multi-threaded scenarios.
    public void writeTest() {
        // Random product selection to simulate real user behavior
        int randomIndex = (int) (Math.random() * testProducts.size());
        MockProduct randomProduct = testProducts.get(randomIndex);
        rankingService.incrementView(randomProduct, 1);
    }
    
    @Benchmark
    @Group("first_redis_read")
    @GroupThreads(2)
    public void readTest(Blackhole blackhole) {
        List<String> topProducts = rankingService.getTopViewedProducts();
        blackhole.consume(topProducts);
    }
    
    // Simulates real-world mixed workload
//    @Benchmark
//    @Group("mixed")
//    @GroupThreads(12) // More writes than reads
//    public void mixedWriteTest() {
//        writeTest();
//    }

//    @Benchmark
//    @Group("mixed")
//    @GroupThreads(4) // Fewer reads
//    public void mixedReadTest(Blackhole blackhole) {
//        readTest(blackhole);
//    }
    
    // Simulates burst write scenarios
//    @Benchmark
//    @Group("burst")
//    public void burstWriteTest() {
//        // Simulate burst of 100 writes to randomly selected products
//        for (int i = 0; i < 100; i++) {
//            int randomIndex = (int) (Math.random() * testProducts.size());
//            Product randomProduct = testProducts.get(randomIndex);
//            rankingService.incrementView(randomProduct);
//        }
//    }
    
    // Simulates heavy read load
//    @Benchmark
//    @Group("heavyRead")
//    @GroupThreads(20) // High number of concurrent reads
//    public void heavyReadTest(Blackhole blackhole) {
//        for (int i = 0; i < 10; i++) { // Multiple reads per thread
//            List<String> topProducts = rankingService.getTopViewedProducts();
//            blackhole.consume(topProducts);
//        }
//    }
}
