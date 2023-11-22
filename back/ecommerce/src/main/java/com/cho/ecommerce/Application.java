package com.cho.ecommerce;

import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@AllArgsConstructor
@SpringBootApplication
public class Application {
    
//    private final FakeDataGenerator dataGenerator;
    
    private final JobLauncher jobLauncher;
    
    private final Job dataInitializationJob; // SimpleBatch에 firstJob 메서드 명과 이름이 일치해야 한다.
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            JobParameters parameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis()) // Unique parameter for each run
                .addLong("numberOfFakeUsers", 10L)
                .addLong("numberOfFakeCategories", 10L)
                .addLong("numberOfFakeOptionsPerCategory", 3L)
                .addLong("numberOfFakeOptionVariationsPerOption", 3L)
                .addLong("numberOfFakeProducts", 10L)
                .addLong("numberOfFakeProductItemsPerProduct", 3L)
                .addLong("numberOfFakeOrderItemsPerOrder", 3L)
                .toJobParameters();
            try {
                jobLauncher.run(dataInitializationJob, parameters);
            } catch (JobExecutionException e) {
                e.printStackTrace();
            }
        };
    }
    
//    @Bean
//    public CommandLineRunner initData() {
//        return args -> {
//            long startTime = System.currentTimeMillis();
//
//            Integer numberOfFakeUsers = 5000;
//            Integer numberOfFakeCategories = 10;
//            Integer numberOfFakeOptions = 3;
//            Integer numberOfFakeOptionsVariations = 3;
//            Integer numberOfFakeProducts = 10;
//            Integer numberOfFakeProductItems = 3;
//            Integer numberOfFakeProductionOptionVariations = numberOfFakeProducts * numberOfFakeProductItems;
//            Integer numberOfFakeOrders = 10;
//            Integer maxProductItemsPerOrder = 3;
//
//            //step1) create fake users
//            dataGenerator.createAuthorities();
//            dataGenerator.createFakeAdmin();
//            dataGenerator.createFakeUser();
//            dataGenerator.generateFakeUsers(numberOfFakeUsers);
//
//            //step2) create fake products
////            dataGenerator.generateFakeCategoryAndOptions(numberOfFakeCategories, numberOfFakeOptions, numberOfFakeOptionsVariations);
////            dataGenerator.generateFake100Products(numberOfFakeProducts, numberOfFakeCategories, numberOfFakeProductItems );
//
//            //step3) create fake orders
////            dataGenerator.generateFakeOrdersAndOrderItems(numberOfFakeOrders, numberOfFakeUsers, maxProductItemsPerOrder, numberOfFakeProductionOptionVariations);
//
//            long endTime = System.currentTimeMillis();
//            long duration = endTime - startTime;
//
//            log.info("Total execution time: " + duration + " ms");
//        };
//    }
}
