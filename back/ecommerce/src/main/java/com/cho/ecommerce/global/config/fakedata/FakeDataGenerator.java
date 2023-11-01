package com.cho.ecommerce.global.config.fakedata;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FakeDataGenerator {
   
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Transactional
    public void createAuthorities() {
        //step1) ROLE_USER, ROLE_ADMIN to Authority table
        if (!authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
            AuthorityEntity userRole = new AuthorityEntity("ROLE_USER");
            authorityRepository.save(userRole);
        }
        if (!authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
            AuthorityEntity adminRole = new AuthorityEntity("ROLE_ADMIN");
            authorityRepository.save(adminRole);
        }
    }

    @Transactional
    public void createAdmin() {
        if (authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
    
            //step1) save user "admin"
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
    
    
    public UserEntity generateROLE_USER() {
        Faker faker = new Faker();

        //step1) save user with ROLE_USER
        if (authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
            UserEntity user = new UserEntity();
            user.setUserId(faker.internet().uuid());
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode("admin"));
            user.setCreated(LocalDateTime.now());
            user.setUpdated(LocalDateTime.now());
            user.setRole("USER");
            user.setEnabled(true);
    
            AddressEntity address = new AddressEntity();
            address.setUser(user);
            address.setStreet(faker.address().streetAddress());
            address.setCity(faker.address().city());
            address.setState(faker.address().state());
            address.setCountry(faker.address().country());
            address.setZipCode(faker.address().zipCode());
            
            user.setAddress(address);
    
    
            AuthorityEntity userRole = authorityRepository.findByAuthority(AuthorityEntity.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
            userAuthorityEntity.setUserEntity(user);
            userAuthorityEntity.setAuthorityEntity(userRole);
            
            user.setUserAuthorities(userAuthorityEntity);
            
            return user;
        }
        return null;
    }
    
    @Transactional
    public void generate1000Users() {
        List<UserEntity> users = new ArrayList<>();
    
        for(int i = 0; i < 100; i++) {
            UserEntity user = generateROLE_USER();
            if (user != null) {
                users.add(user);
            }
        }
        userRepository.saveAll(users);
    }
}
