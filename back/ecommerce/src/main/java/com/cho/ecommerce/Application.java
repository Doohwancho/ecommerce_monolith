package com.cho.ecommerce;

import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@AllArgsConstructor
@SpringBootApplication
public class Application {
    
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner initData(
        AuthorityRepository authorityRepository,
        CategoryRepository categoryRepository,
        OptionRepository optionRepository,
        OptionVariationRepository optionVariationRepository) {
        return args -> {
            //step1) ROLE_USER, ROLE_ADMIN to Authority table
            if (!authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
                AuthorityEntity userRole = new AuthorityEntity("ROLE_USER");
                authorityRepository.save(userRole);
            }
            if (!authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                AuthorityEntity adminRole = new AuthorityEntity("ROLE_ADMIN");
                authorityRepository.save(adminRole);
            }
            
            //step2) create admin with id:admin pw:admin
            if (authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                UserEntity admin = new UserEntity();
                admin.setUserId("admin");
                admin.setName("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setCreated(LocalDateTime.now());
                admin.setUpdated(LocalDateTime.now());
                admin.setRole("ADMIN");
                admin.setEnabled(true);
    
                UserEntity savedUserEntity = userRepository.save(admin);
                
                AuthorityEntity userRole = authorityRepository.findByAuthority(
                        AuthorityEntity.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
    
                UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
                userAuthorityEntity.setUserEntity(savedUserEntity);
                userAuthorityEntity.setAuthorityEntity(userRole);
    
                userAuthorityRepository.save(userAuthorityEntity);
            }
            
            //step3) Initialize basic categories
            CategoryEntity electronics = new CategoryEntity();
            electronics.setCategoryCode("ELEC");
            electronics.setName("Electronics");
            categoryRepository.save(electronics);
    
            CategoryEntity clothing = new CategoryEntity();
            clothing.setCategoryCode("CLOTH");
            clothing.setName("Clothing");
            categoryRepository.save(clothing);
    
            // Initialize basic options for Electronics
            OptionEntity colorOption = new OptionEntity();
            colorOption.setValue("Color");
            colorOption.setCategory(electronics);
            optionRepository.save(colorOption);
    
            OptionEntity sizeOption = new OptionEntity();
            sizeOption.setValue("Size");
            sizeOption.setCategory(electronics);
            optionRepository.save(sizeOption);
    
            // Initialize basic options for Clothing
            OptionEntity clothingSizeOption = new OptionEntity();
            clothingSizeOption.setValue("Size");
            clothingSizeOption.setCategory(clothing);
            optionRepository.save(clothingSizeOption);
    
            OptionEntity clothingColorOption = new OptionEntity();
            clothingColorOption.setValue("Color");
            clothingColorOption.setCategory(clothing);
            optionRepository.save(clothingColorOption);
    
            // Initialize option variations for Electronics Color
            OptionVariationEntity redColor = new OptionVariationEntity();
            redColor.setValue("Red");
            redColor.setOption(colorOption);
            optionVariationRepository.save(redColor);
    
            OptionVariationEntity blueColor = new OptionVariationEntity();
            blueColor.setValue("Blue");
            blueColor.setOption(colorOption);
            optionVariationRepository.save(blueColor);
    
            // Initialize option variations for Electronics Size
            OptionVariationEntity smallSize = new OptionVariationEntity();
            smallSize.setValue("Small");
            smallSize.setOption(sizeOption);
            optionVariationRepository.save(smallSize);
    
            OptionVariationEntity largeSize = new OptionVariationEntity();
            largeSize.setValue("Large");
            largeSize.setOption(sizeOption);
            optionVariationRepository.save(largeSize);
        };
    }
    
}
