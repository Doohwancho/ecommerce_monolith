package com.cho.ecommerce.global.config.fakedata;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.product.domain.DiscountType;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.DiscountRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import com.cho.ecommerce.domain.product.repository.ProductItemRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FakeDataGenerator {
    private final Logger log = LoggerFactory.getLogger(FakeDataGenerator.class);
    
    private final Faker faker = new Faker();
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private final CategoryRepository categoryRepository;
    
    private final OptionRepository optionRepository;
    
    private final OptionVariationRepository optionVariationRepository;
    
    private final ProductRepository productRepository;
    
    private final ProductItemRepository productItemRepository;
    private final DiscountRepository discountRepository;
    
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
    public void generateFakeUsers(Integer numberOfUsers) {
        List<UserEntity> users = new ArrayList<>();
    
        for(int i = 0; i < numberOfUsers; i++) {
            UserEntity user = generateROLE_USER();
            if (user != null) {
                users.add(user);
            }
        }
        userRepository.saveAll(users);
    }
    
    @Transactional
    public void generateFakeCategoryAndOptions(Integer numberOfFakeCategories, Integer numberOfFakeOptions, Integer numberOfFakeOptionsVariations) {
        List<CategoryEntity> lists = new ArrayList<>();
    
        // Generate categories
        for (int i = 0; i < numberOfFakeCategories; i++) {
            CategoryEntity category = new CategoryEntity();
            category.setCategoryCode(faker.code().asin());
            category.setName(faker.commerce().department());
            
            // Generate options for each category
            Set<OptionEntity> options = new HashSet<>();
            for (int j = 0; j < numberOfFakeOptions; j++) {
                OptionEntity option = new OptionEntity();
                option.setValue(faker.commerce().material());
                option.setCategory(category);
                option.setOptionVariations(new ArrayList<>());
                optionRepository.save(option);
                options.add(option);
                
                // Generate option variations for each option
                for (int k = 0; k < numberOfFakeOptionsVariations; k++) {
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
    
    @Transactional
    public void generateFake100Products(Integer numberOfFakeProducts, Integer numberOfFakeCategories) {
        for (int i = 0; i < numberOfFakeProducts; i++) { //카테고리수가 10개니까, 1000개가 max
            //step1) create product
            ProductEntity product = new ProductEntity();
            product.setName(faker.commerce().productName());
            product.setDescription(faker.lorem().sentence());
            product.setRating(faker.number().randomDouble(1, 1, 5));
            product.setRatingCount(faker.number().numberBetween(1, 1000));
            
            
            //step2) get category for the product
            int index = i % numberOfFakeCategories;
            if (index == 0) {
                index = numberOfFakeCategories;
            }
            CategoryEntity category = categoryRepository.findByCategoryId(Long.valueOf(index)); //10개의 카테고리를 순차적으로 가져온다.
            product.setCategory(category);
    
            
            //step3) get option and option variations from category
            Map<OptionEntity, OptionVariationEntity> map = new HashMap<>();
    
            List<OptionEntity> optionEntityList = optionRepository.findByCategory_CategoryId(
                category.getCategoryId());
            
            optionEntityList.forEach(e -> {
                List<OptionVariationEntity> optionVariationList = optionVariationRepository.findByOption_OptionId(
                    e.getOptionId());
                if(e.getOptionVariations() == null) {
                    e.setOptionVariations(new ArrayList<>());
                }
                e.getOptionVariations().add(optionVariationList.get(0));
            });
    
            
            //step4) create productItems for each product
            Set<ProductItemEntity> productItems = new HashSet<>();

            for (int j = 0; j < 3; j++) {
                ProductItemEntity productItem = new ProductItemEntity();

                OptionEntity option = optionEntityList.get(j);
                productItem.setOption(option.getValue());
                productItem.setOptionVariation(option.getOptionVariations().get(0).getValue());
                productItem.setQuantity(faker.number().numberBetween(1, 100));
                productItem.setPrice(faker.number().randomDouble(2, 1, 10000));
                productItem.setProduct(product);
                productItemRepository.save(productItem);
                productItems.add(productItem);

                //step5) Generate discounts for each product item
                DiscountEntity discount = new DiscountEntity();
                discount.setDiscountType(
                    DiscountType.values()[faker.number().numberBetween(0, DiscountType.values().length)]);
                discount.setDiscountValue(new BigDecimal(faker.number().randomDouble(2, 1, 100)));
                discount.setStartDate(faker.date().past(10, TimeUnit.DAYS));
                discount.setEndDate(faker.date().future(10, TimeUnit.DAYS));
                discount.setProductItem(productItem);
                discountRepository.save(discount);
            }
            product.setProductItems(productItems);
            productRepository.save(product);
        }
    }
}
