package com.cho.ecommerce;

import com.cho.ecommerce.global.config.fakedata.step1_jpa_saveAll.JpaFakeDataGenerator;
import com.cho.ecommerce.global.config.fakedata.step2_jdbc_bulk_insert.JdbcFakeDataGenerator;
import com.cho.ecommerce.global.util.DatabaseCleanup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
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
    
    private final JpaFakeDataGenerator jpaDataGenerator;
    private final JdbcFakeDataGenerator jdbcDataGenerator;
    private final DatabaseCleanup databaseCleanup;
    
    private final JobLauncher jobLauncher;
    
    private final Job dataInitializationJob; // SimpleBatch에 firstJob 메서드 명과 이름이 일치해야 한다.
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner initData() {
        return args -> {

//            databaseCleanup.afterPropertiesSet();
//            databaseCleanup.execute();
            
            long startTime = System.currentTimeMillis();
            
            //total 100-ish rows
//            int batchSize = 1000;
//            int numberOfUsers = 10; //2,000 * 3 (user, address, user_authority)
//            int numberOfLowCategoriesPerMidCategories = 5; //75
//            int numberOfOptions = 3; //180
//            int numberOfOptionVariations = 3; //540
//            int numberOfProducts = 10; //4,000
//            int numberOfProductItemsPerProduct = 3; //12,000
//            int numberOfDiscountsPerProductItem = 1; //12,000
//            int numberOfProductOptionVariationPerProductItem = 1; //12,000
//            int numberOfOrderItemsPerOrder = 2; //2000 + 4000 (order, orderItem)
            
            //total 53,000 rows
            int batchSize = 1000;
            int numberOfUsers = 2000; //2,000 * 3 (user, address, user_authority)
            int numberOfLowCategoriesPerMidCategories = 5; //75
            int numberOfOptions = 3; //180
            int numberOfOptionVariations = 3; //540
            int numberOfProducts = 4000; //4,000
            int numberOfProductItemsPerProduct = 3; //12,000
            int numberOfDiscountsPerProductItem = 1; //12,000
            int numberOfProductOptionVariationPerProductItem = 1; //12,000
            int numberOfOrderItemsPerOrder = 2; //2000 + 4000 (order, orderItem)
            
            //total 1,040,000 rows
//            int batchSize = 1000;
//            int numberOfUsers = 40000; //120,000
//            int numberOfLowCategoriesPerMidCategories = 5;
//            int numberOfOptions = 3;
//            int numberOfOptionVariations = 3;
//            int numberOfProducts = 80000; //80,000
//            int numberOfProductItemsPerProduct = 3; //240,000
//            int numberOfDiscountsPerProductItem = 1; //240,000
//            int numberOfProductOptionVariationPerProductItem = 1; //240,000
//            int numberOfOrderItemsPerOrder = 2; //40,000 + 80,000
            
            jdbcDataGenerator.bulkInsert(numberOfUsers, numberOfLowCategoriesPerMidCategories,
                numberOfOptions, numberOfOptionVariations, numberOfProducts,
                numberOfProductItemsPerProduct,
                numberOfDiscountsPerProductItem,
                numberOfProductOptionVariationPerProductItem,
                numberOfOrderItemsPerOrder,
                batchSize);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("Total execution time: " + duration + " ms");
        };
    }

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
    
    //주의!
    //OrderEntity.java에
    //@NotEmpty(message = "Order must have at least one item")
    //를 주석 치고 넣어야 한다!
    //다 넣고 주석 없애야 한다!
//    @Bean
//    public CommandLineRunner initData() {
//        return args -> {
//            long startTime = System.currentTimeMillis();
//
//            Integer numberOfFakeUsers = 1; //6000 rows total
//            Integer numberOfFakeCategories = 1; //75 rows total
//            Integer numberOfFakeOptions = 1; //180 rows
//            Integer numberOfFakeOptionsVariations = 1; //540 rows
//            Integer numberOfFakeProducts = 1;
//            Integer numberOfFakeProductItems = 1; //12000 + 12000 (discount) rows total
//            Integer numberOfFakeProductionOptionVariations =
//                numberOfFakeProducts * numberOfFakeProductItems; //12000 rows
//            Integer numberOfFakeOrders = 1; //2000 rows
//            Integer maxProductItemsPerOrder = 1; //4000 rows
//
////            Integer numberOfFakeUsers = 2000; //6000 rows total
////            Integer numberOfFakeCategories = 10; //75 rows total
////            Integer numberOfFakeOptions = 3; //180 rows
////            Integer numberOfFakeOptionsVariations = 3; //540 rows
////            Integer numberOfFakeProducts = 4000;
////            Integer numberOfFakeProductItems = 3; //12000 + 12000 (discount) rows total
////            Integer numberOfFakeProductionOptionVariations =
////                numberOfFakeProducts * numberOfFakeProductItems; //12000 rows
////            Integer numberOfFakeOrders = 2000; //2000 rows
////            Integer maxProductItemsPerOrder = 2; //4000 rows
//
//            //step1) create fake users
//            jpaDataGenerator.createAuthorities();
////            dataGenerator.createFakeAdmin();
////            dataGenerator.createFakeUser();
//            jpaDataGenerator.generateFakeUsers(numberOfFakeUsers);
//
//            //step2) create fake products
//            jpaDataGenerator.generateFakeCategoryAndOptions(numberOfFakeCategories, //DEPRECATED
//                numberOfFakeOptions, numberOfFakeOptionsVariations);
//            jpaDataGenerator.generateFakeProducts(numberOfFakeProducts, numberOfFakeCategories,
//                numberOfFakeProductItems);
//
//            //step3) create fake orders
//            jpaDataGenerator.generateFakeOrdersAndOrderItems(numberOfFakeOrders, numberOfFakeUsers,
//                maxProductItemsPerOrder, numberOfFakeProductionOptionVariations);
//
//            long endTime = System.currentTimeMillis();
//            long duration = endTime - startTime;
//
//            log.info("Total execution time: " + duration + " ms");
//        };
//    }
}
