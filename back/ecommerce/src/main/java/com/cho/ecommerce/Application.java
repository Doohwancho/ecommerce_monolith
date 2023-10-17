package com.cho.ecommerce;

import com.cho.ecommerce.domain.Authority;
import com.cho.ecommerce.repository.AuthorityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner initData(AuthorityRepository authorityRepository) {
        return args -> {
            if (!authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
                Authority userRole = new Authority("ROLE_USER");
                authorityRepository.save(userRole);
            }
            if (!authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                Authority adminRole = new Authority("ROLE_ADMIN");
                authorityRepository.save(adminRole);
            }
        };
    }
    
}
