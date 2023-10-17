package com.cho.ecommerce;

import com.cho.ecommerce.domain.Authority;
import com.cho.ecommerce.domain.User;
import com.cho.ecommerce.domain.UserAuthority;
import com.cho.ecommerce.repository.AuthorityRepository;
import com.cho.ecommerce.repository.UserAuthorityRepository;
import com.cho.ecommerce.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
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
    public CommandLineRunner initData(AuthorityRepository authorityRepository) {
        return args -> {
            //step1) ROLE_USER, ROLE_ADMIN to Authority table
            if (!authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
                Authority userRole = new Authority("ROLE_USER");
                authorityRepository.save(userRole);
            }
            if (!authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                Authority adminRole = new Authority("ROLE_ADMIN");
                authorityRepository.save(adminRole);
            }
            
            //step2) create admin with id:admin pw:admin
            if (authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                User admin = new User();
                admin.setUserId("admin");
                admin.setName("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setCreated(LocalDateTime.now());
                admin.setUpdated(LocalDateTime.now());
                admin.setRole("ADMIN");
                admin.setEnabled(true);
    
                User savedUser = userRepository.save(admin);
                
                Authority userRole = authorityRepository.findByAuthority(Authority.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
    
                UserAuthority userAuthority = new UserAuthority();
                userAuthority.setUser(savedUser);
                userAuthority.setAuthority(userRole);
    
                userAuthorityRepository.save(userAuthority);
            }
        };
    }
    
}
