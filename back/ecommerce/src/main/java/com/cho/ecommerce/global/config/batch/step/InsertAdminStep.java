package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class InsertAdminStep {
    private final Logger log = LoggerFactory.getLogger(InsertAdminStep.class);
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    
    @Bean
    public Tasklet createAdminTasklet() {
        return (contribution, chunkContext) -> {
            if (authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                UserEntity existingAdmin = userRepository.findByUsername("admin");

                if (existingAdmin == null) { //to avoid duplicate key error (UserEntity.username is @Unique) - SQL Error: 1062, SQLState: 23000
                    //step1) save user "admin"
                    UserEntity admin = new UserEntity();
                    admin.setUsername("admin");
                    admin.setName("admin");
                    admin.setEmail("admin@admin.com");
                    admin.setPassword(passwordEncoder.encode("admin"));
                    admin.setCreated(LocalDateTime.now());
                    admin.setUpdated(LocalDateTime.now());
                    admin.setRole("ROLE_ADMIN");
                    admin.setEnabled(true);
                    admin.setFailedAttempt(0);

                    UserEntity savedUserEntity = userRepository.save(admin);

                    //step2) save AuthorityEntity "ROLE_ADMIN"
                    AuthorityEntity userRole = authorityRepository.findByAuthority(
                            AuthorityEntity.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

                    UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
                    userAuthorityEntity.setUserEntity(savedUserEntity);
                    userAuthorityEntity.setAuthorityEntity(userRole);

                    userAuthorityRepository.save(userAuthorityEntity);
                }
            }
            return RepeatStatus.FINISHED;
        };
    }
    
    
    @Bean
    public Step createAdminStep() {
        return stepBuilderFactory.get("createAdminStep")
            .tasklet(createAdminTasklet())
            .build();
    }
}
