package com.cho.ecommerce;

import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
@SpringBootApplication
public class Application {
    
    private final FakeDataGenerator dataGenerator;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            Integer numberOfFakeUsers = 10;
            Integer numberOfFakeCategories = 10;
            Integer numberOfFakeOptions = 3;
            Integer numberOfFakeOptionsVariations = 3;
            Integer numberOfFakeProducts = 10;
            Integer numberOfFakeProductItems = 3;
            Integer numberOfFakeProductionOptionVariations = numberOfFakeProducts * numberOfFakeProductItems;
            Integer numberOfFakeOrders = 10;
            Integer maxProductItemsPerOrder = 3;
            
            //step1) create fake users
            dataGenerator.createAuthorities();
            dataGenerator.createFakeAdmin();
            dataGenerator.generateFakeUsers(numberOfFakeUsers);
            
            //step2) create fake products
            dataGenerator.generateFakeCategoryAndOptions(numberOfFakeCategories, numberOfFakeOptions, numberOfFakeOptionsVariations);
            dataGenerator.generateFake100Products(numberOfFakeProducts, numberOfFakeCategories, numberOfFakeProductItems );
            
            //step3) create fake orders
            dataGenerator.generateFakeOrdersAndOrderItems(numberOfFakeOrders, numberOfFakeUsers, maxProductItemsPerOrder, numberOfFakeProductionOptionVariations);
        };
    }
}
