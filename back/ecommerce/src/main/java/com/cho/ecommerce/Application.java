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
            dataGenerator.createAuthorities();
            dataGenerator.createFakeAdmin();
            dataGenerator.generateFake1000Users();
            dataGenerator.generateFakeCategoryAndOptions();
        };
    }
    
}
