package com.cho.ecommerce.global.config.fakedata;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FakeDataGenerator {
    
    private final Faker faker = new Faker();
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private final CategoryRepository categoryRepository;
    
    private final OptionRepository optionRepository;
    
    private final OptionVariationRepository optionVariationRepository;
    
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
    public void createFakeAdmin() {
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
    public void generateFake1000Users() {
        List<UserEntity> users = new ArrayList<>();
    
        for(int i = 0; i < 10; i++) {
            UserEntity user = generateROLE_USER();
            if (user != null) {
                users.add(user);
            }
        }
        userRepository.saveAll(users);
    }
    
    @Transactional
    public void generateFakeCategoryAndOptions() {
        List<CategoryEntity> lists = new ArrayList<>();
    
        // Generate categories
        for (int i = 0; i < 10; i++) {
            CategoryEntity category = new CategoryEntity();
            category.setCategoryCode(faker.code().asin());
            category.setName(faker.commerce().department());
            
            // Generate options for each category
            Set<OptionEntity> options = new HashSet<>();
            for (int j = 0; j < 5; j++) {
                OptionEntity option = new OptionEntity();
                option.setValue(faker.commerce().material());
                option.setCategory(category);
                option.setOptionVariations(new HashSet<>());
                optionRepository.save(option);
                options.add(option);
                
                // Generate option variations for each option
                for (int k = 0; k < 3; k++) {
                    OptionVariationEntity optionVariation = new OptionVariationEntity();
                    optionVariation.setValue(faker.color().name());
                    optionVariation.setOption(option);
                    optionVariationRepository.save(optionVariation);
                    
                    option.getOptionVariations().add(optionVariation);
                }
            }
            category.setOptionEntities(options);
            lists.add(category);
        }
        categoryRepository.saveAll(lists);
    }
}
