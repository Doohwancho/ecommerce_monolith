package com.cho.ecommerce;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import java.util.ArrayList;
import java.util.List;
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
            dataGenerator.createAdmin();
            dataGenerator.generate1000Users();
            
            
            
//            //step3) Initialize basic categories
//            CategoryEntity electronics = new CategoryEntity();
//            electronics.setCategoryCode("ELEC");
//            electronics.setName("Electronics");
//            categoryRepository.save(electronics);
//
//            CategoryEntity clothing = new CategoryEntity();
//            clothing.setCategoryCode("CLOTH");
//            clothing.setName("Clothing");
//            categoryRepository.save(clothing);
//
//            // Initialize basic options for Electronics
//            OptionEntity colorOption = new OptionEntity();
//            colorOption.setValue("Color");
//            colorOption.setCategory(electronics);
//            optionRepository.save(colorOption);
//
//            OptionEntity sizeOption = new OptionEntity();
//            sizeOption.setValue("Size");
//            sizeOption.setCategory(electronics);
//            optionRepository.save(sizeOption);
//
//            // Initialize basic options for Clothing
//            OptionEntity clothingSizeOption = new OptionEntity();
//            clothingSizeOption.setValue("Size");
//            clothingSizeOption.setCategory(clothing);
//            optionRepository.save(clothingSizeOption);
//
//            OptionEntity clothingColorOption = new OptionEntity();
//            clothingColorOption.setValue("Color");
//            clothingColorOption.setCategory(clothing);
//            optionRepository.save(clothingColorOption);
//
//            // Initialize option variations for Electronics Color
//            OptionVariationEntity redColor = new OptionVariationEntity();
//            redColor.setValue("Red");
//            redColor.setOption(colorOption);
//            optionVariationRepository.save(redColor);
//
//            OptionVariationEntity blueColor = new OptionVariationEntity();
//            blueColor.setValue("Blue");
//            blueColor.setOption(colorOption);
//            optionVariationRepository.save(blueColor);
//
//            // Initialize option variations for Electronics Size
//            OptionVariationEntity smallSize = new OptionVariationEntity();
//            smallSize.setValue("Small");
//            smallSize.setOption(sizeOption);
//            optionVariationRepository.save(smallSize);
//
//            OptionVariationEntity largeSize = new OptionVariationEntity();
//            largeSize.setValue("Large");
//            largeSize.setOption(sizeOption);
//            optionVariationRepository.save(largeSize);
        };
    }
    
}
