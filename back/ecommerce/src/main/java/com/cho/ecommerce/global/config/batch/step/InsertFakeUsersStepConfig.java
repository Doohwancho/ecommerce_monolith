package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class InsertFakeUsersStepConfig {
    private final Logger log = LoggerFactory.getLogger(InsertFakeUsersStepConfig.class);
    
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  BCryptPasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    
    private AuthorityEntity roleUserAuthority;
    private boolean enabled;
    
    
    private AuthorityEntity getRoleUserAuthority() {
        if (this.roleUserAuthority == null) {
            this.roleUserAuthority = authorityRepository.findByAuthority(AuthorityEntity.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        }
        return this.roleUserAuthority;
    }
    
    public String sizeTrimmer(String str, int size){
        int len = str.length();
        if(len >= size) {
            return str.substring(len-size, len-1);
        }
        return str;
    }
    
    public UserEntity generateRandomROLE_USER() {
        UserEntity user = new UserEntity();
        String userName = sizeTrimmer(UUID.randomUUID().toString(), DatabaseConstants.MEMBER_USERNAME_SIZE); //use UUID to avoid duplicate of userId
        String name = sizeTrimmer(faker.name().fullName(), DatabaseConstants.MEMBER_NAME_SIZE);
        String email = sizeTrimmer(faker.internet().emailAddress(), DatabaseConstants.EMAIL_SIZE);
    
        user.setUsername(userName);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        user.setRole("ROLE_USER");
        user.setEnabled(enabled);
        enabled = !enabled;
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
    
        UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
        userAuthorityEntity.setUserEntity(user);
        userAuthorityEntity.setAuthorityEntity(getRoleUserAuthority());
    
        user.setUserAuthorities(userAuthorityEntity);
    
        return user;
    }
    
    
    @Bean
    @StepScope
    public ItemReader<UserEntity> generateRandomROLE_USERReader(@Value("#{jobParameters['numberOfFakeUsers']}") Long numberOfFakeUsers) {
        return new ItemReader<UserEntity>() {
            private final int NUMBER_OF_FAKE_USERS = numberOfFakeUsers.intValue();
            private int userCount = 0;
        
            @Override
            public UserEntity read() {
                if (userCount < NUMBER_OF_FAKE_USERS) {
                    userCount++;
                    return generateRandomROLE_USER();
                } else {
                    return null; // Return null to indicate the end of data
                }
            }
        };
    }
    
    @Bean
    public ItemWriter<UserEntity> InsertUsersWriter(EntityManager entityManager) {
        return new ItemWriter<UserEntity>() {
            @Override
            public void write(List<? extends UserEntity> users) {
                userRepository.saveAll(users);
//                entityManager.flush(); //오해: Flushing within a transaction does not commit the transaction
            }
        };
    }
    
    @Bean
    public Step generateFakeUserStep(StepBuilderFactory stepBuilderFactory,
//        PlatformTransactionManager transactionManager,
        ItemReader<UserEntity> generateRandomROLE_USERReader,
        ItemWriter<UserEntity> InsertUsersWriter) {
        
//        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
//        attribute.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
//        attribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        attribute.setTimeout(30); // 30 seconds
        
        return stepBuilderFactory.get("insertFakeUserStep")
            .<UserEntity, UserEntity>chunk(1000) //<UserEntity, UserEntity>에서 첫번째 인자는 .reader()가 리턴하는 인자이고, 두번째 인자는 writer()가 리턴하는 인자이다.
            .reader(generateRandomROLE_USERReader) //Spring Batch manages transactions at the chunk level
            .writer(InsertUsersWriter)
//            .transactionManager(transactionManager)
//            .transactionAttribute(attribute)
            .build();
    }
}