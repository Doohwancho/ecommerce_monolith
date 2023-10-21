package com.cho.ecommerce;

import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
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
    public CommandLineRunner initData(AuthorityRepository authorityRepository) {
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
        };
    }
    
}
