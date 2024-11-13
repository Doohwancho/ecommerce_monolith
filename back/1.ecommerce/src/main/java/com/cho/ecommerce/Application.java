package com.cho.ecommerce;

import com.cho.ecommerce.domain.product.domain.ProductViewCounter;
import com.cho.ecommerce.global.config.bulk_insert.fakedata.BulkInsertController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@Slf4j
@EnableBatchProcessing
@EnableCaching
@AllArgsConstructor
@SpringBootApplication
public class Application {
    
    private final BulkInsertController bulkInsertController;
    private final ProductViewCounter productViewCounter;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(
        @Value("${app.product.max-id:10000}") int MAX_PRODUCT_ID) {
        int bulkInsertAmount = MAX_PRODUCT_ID;
        return args -> {
            bulkInsertController.bulkInsert(String.valueOf(bulkInsertAmount));
            productViewCounter.initialize();
        };
    }
    
    /**
     * Deprecated
     * spring-batch로 bulk-insert하는 코드
     * 더 효율적인 방법을 찾아서 이제는 안쓴다.
     */
    
    //DEPRECATED
    //spring-batch for bulk-insert code
//    private final JpaFakeDataGenerator jpaDataGenerator;
//    private final DatabaseCleanup databaseCleanup;
//    private final JobLauncher jobLauncher;
//    private final Job dataInitializationJob; // SimpleBatch에 firstJob 메서드 명과 이름이 일치해야 한다.

//    @Bean
//    public CommandLineRunner commandLineRunner() {
//        return args -> {
//            JobParameters parameters = new JobParametersBuilder()
//                .addLong("run.id", System.currentTimeMillis()) // Unique parameter for each run
//                .addLong("numberOfFakeUsers", 2000L)
//                .addLong("numberOfFakeCategories", 10L)
//                .addLong("numberOfFakeOptionsPerCategory", 3L)
//                .addLong("numberOfFakeOptionVariationsPerOption", 3L)
//                .addLong("numberOfFakeProducts", 4000L)
//                .addLong("numberOfFakeProductItemsPerProduct", 3L)
//                .addLong("numberOfFakeOrderItemsPerOrder", 2L)
//                .toJobParameters();
//            try {
//                jobLauncher.run(dataInitializationJob, parameters);
//            } catch (JobExecutionException e) {
//                e.printStackTrace();
//            }
//        };
//    }
}
