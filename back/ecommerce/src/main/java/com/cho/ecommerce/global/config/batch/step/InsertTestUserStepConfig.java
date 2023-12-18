package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.time.LocalDateTime;
import net.datafaker.Faker;
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
public class InsertTestUserStepConfig {
    
    private final Logger log = LoggerFactory.getLogger(InsertAdminStepConfig.class);
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
    
    private final Faker faker = new Faker();
    
    public String sizeTrimmer(String str, int size){
        int len = str.length();
        if(len >= size) {
            return str.substring(len-size, len-1);
        }
        return str;
    }
    @Bean
    public Tasklet createTestUserTasklet() {
        return (contribution, chunkContext) -> {
            if (authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
                
                UserEntity existingTestUser = userRepository.findByUsername("testUser");
                
                if (existingTestUser
                    == null) { //to avoid duplicate key error (UserEntity.username is @Unique) - SQL Error: 1062, SQLState: 23000
                    //step1) save user "testUser"
                    UserEntity user = new UserEntity();
                    user.setUsername("testUser");
                    user.setName("testUser");
                    user.setEmail("testUser@testUser.com");
                    user.setPassword(passwordEncoder.encode("password"));
                    user.setCreated(LocalDateTime.now());
                    user.setUpdated(LocalDateTime.now());
                    user.setRole("ROLE_USER");
                    user.setEnabled(true);
                    user.setFailedAttempt(0);
    
                    AddressEntity address = new AddressEntity();
                    address.setUser(user);
    
                    String streetAddress = sizeTrimmer(faker.address().streetAddress(), DatabaseConstants.STREET_SIZE);
                    String city = sizeTrimmer(faker.address().city(), DatabaseConstants.CITY_SIZE);
                    String state = sizeTrimmer(faker.address().state(), DatabaseConstants.STATE_SIZE);
                    String country = sizeTrimmer(faker.address().country(), DatabaseConstants.COUNTRY_SIZE);
                    String zipCode = sizeTrimmer(faker.address().zipCode(), DatabaseConstants.ZIPCODE_SIZE);
    
                    address.setStreet(streetAddress);
                    address.setCity(city);
                    address.setState(state);
                    address.setCountry(country);
                    address.setZipCode(zipCode);
    
                    user.setAddress(address);
                    
                    UserEntity savedUserEntity = userRepository.save(user);
                    
                    //step2) save AuthorityEntity "ROLE_ADMIN"
                    AuthorityEntity userRole = authorityRepository.findByAuthority(
                            AuthorityEntity.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
                    
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
    public Step createTestUserStep() {
        return stepBuilderFactory.get("createTestUserStep")
            .tasklet(createTestUserTasklet())
            .build();
    }
}
