package com.cho.ecommerce.global.config.fakedata;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import com.cho.ecommerce.domain.order.repository.OrderItemRepository;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.product.domain.DiscountType;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.DiscountRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import com.cho.ecommerce.domain.product.repository.ProductItemRepository;
import com.cho.ecommerce.domain.product.repository.ProductOptionVariationRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.dialect.Database;
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
    private final ProductOptionVariationRepository productOptionVariationRepository;
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    
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
    
    @Transactional
    public void createFakeUser() {
        if (authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
            
            //step1) save user "admin"
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
    
    public UserEntity generateRandomROLE_USER() {
        
        if (authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
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
            
            AuthorityEntity userRole = authorityRepository.findByAuthority(
                    AuthorityEntity.ROLE_USER)
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
        
        for (int i = 0; i < numberOfUsers; i++) {
            UserEntity user = generateRandomROLE_USER();
            if (user != null) {
                users.add(user);
            }
        }
        userRepository.saveAll(users);
    }
    
    
    @Transactional
    public void generateFakeCategoryAndOptions(Integer numberOfFakeCategories,
        Integer numberOfFakeOptions, Integer numberOfFakeOptionsVariations) {
        List<CategoryEntity> lists = new ArrayList<>();
        
        // Generate categories
        for (int i = 0; i < numberOfFakeCategories; i++) {
            CategoryEntity category = new CategoryEntity();
            String categoryCode = sizeTrimmer(faker.code().asin(), DatabaseConstants.CATEGORY_CODE_SIZE);
            String categoryName = sizeTrimmer(faker.commerce().department(), DatabaseConstants.CATEGORY_NAME_SIZE);
            
            category.setCategoryCode(categoryCode);
            category.setName(categoryName);
            
            // Generate options for each category
            Set<OptionEntity> options = new HashSet<>();
            for (int j = 0; j < numberOfFakeOptions; j++) {
                OptionEntity option = new OptionEntity();
                String optionValue = sizeTrimmer(faker.commerce().material(), DatabaseConstants.OPTION_VALUE_SIZE);
                
                option.setValue(optionValue);
                option.setCategory(category);
                option.setOptionVariations(new ArrayList<>());
                optionRepository.save(option);
                options.add(option);
                
                // Generate option variations for each option
                for (int k = 0; k < numberOfFakeOptionsVariations; k++) {
                    OptionVariationEntity optionVariation = new OptionVariationEntity();
                    
                    String optionVariationValue = sizeTrimmer(faker.color().name(), DatabaseConstants.OPTION_VARIATION_VALUE_SIZE);
                    
                    optionVariation.setValue(optionVariationValue);
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
    public void generateFake100Products(Integer numberOfFakeProducts,
        Integer numberOfFakeCategories, Integer numberOfFakeProductItems) {
        for (int i = 0; i < numberOfFakeProducts; i++) { //카테고리수가 10개니까, 1000개가 max
            //step1) create product
            ProductEntity product = new ProductEntity();
            
            String productName = sizeTrimmer(faker.commerce().productName(), DatabaseConstants.PRODUCT_NAME_SIZE);
            String productDescription = sizeTrimmer(faker.lorem().sentence(), DatabaseConstants.PRODUCT_DESCRIPTION_SIZE);
            Double productRating = faker.number().randomDouble(1, 1, 5);
            Integer productRatingCount =faker.number().numberBetween(1, 1000);
            
            product.setName(productName);
            product.setDescription(productDescription);
            product.setRating(productRating);
            product.setRatingCount(productRatingCount);
            
            //step2) get category for the product
            int index = i % numberOfFakeCategories;
            if (index == 0) {
                index = numberOfFakeCategories;
            }
            CategoryEntity category = categoryRepository.findByCategoryId(
                Long.valueOf(index)); //10개의 카테고리를 순차적으로 가져온다.
            product.setCategory(category);
            
            //step3) get option and option variations from category
            List<OptionEntity> optionEntityList = optionRepository.findByCategory_CategoryId(
                category.getCategoryId());
            
            optionEntityList.forEach(option -> {
                List<OptionVariationEntity> optionVariationList = optionVariationRepository.findByOption_OptionId(
                    option.getOptionId());
                if (option.getOptionVariations() == null) {
                    option.setOptionVariations(new ArrayList<>());
                }
                option.getOptionVariations().add(optionVariationList.get(0));
            });
            
            //step4) create productItems for each product
            Set<ProductItemEntity> productItems = new HashSet<>();
            
            for (int j = 0; j < numberOfFakeProductItems; j++) {
                ProductItemEntity productItem = new ProductItemEntity();
                
                Integer productItemQuantity = faker.number().numberBetween(1, 100);
                Double productItemPrice = faker.number().randomDouble(2, 1, 10000);
                
                productItem.setQuantity(productItemQuantity);
                productItem.setPrice(productItemPrice);
                productItem.setProduct(product);
                
                ProductItemEntity savedProductItem = productItemRepository.save(productItem);
                productItems.add(productItem);
                
                //step5) create product_option_variation
                ProductOptionVariationEntity productOptionVariationEntity = new ProductOptionVariationEntity();
                OptionVariationEntity optionVariations = optionEntityList.get(j)
                    .getOptionVariations().get(0);
                productOptionVariationEntity.setOptionVariation(optionVariations);
                productOptionVariationEntity.setProductItem(savedProductItem);
                productOptionVariationRepository.save(productOptionVariationEntity);
                
                //step6) Generate discounts for each product item
                DiscountEntity discount = new DiscountEntity();
                discount.setDiscountType(
                    DiscountType.values()[faker.number()
                        .numberBetween(0, DiscountType.values().length)]);
                discount.setDiscountValue(faker.number().randomDouble(2, 1, 100));
//                discount.setStartDate(faker.date().past(10, TimeUnit.DAYS));
                discount.setStartDate(
                    faker.date().past(10, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())
                        .toOffsetDateTime());
//                discount.setEndDate(faker.date().future(10, TimeUnit.DAYS));
                discount.setEndDate(faker.date().future(10, TimeUnit.DAYS).toInstant()
                    .atZone(ZoneId.systemDefault()).toOffsetDateTime());
                discount.setProductItem(productItem);
                discountRepository.save(discount);
            }
            product.setProductItems(productItems);
            productRepository.save(product);
        }
    }
    
    
    @Transactional
    public void generateFakeOrdersAndOrderItems(Integer numberOfOrders, Integer numberOfFakeUsers,
        Integer maxProductItemsPerOrder, Integer numberOfFakeProductionOptionVariations) {
        for (int i = 0; i < numberOfOrders; i++) {
            // Create a fake order
            OrderEntity order = new OrderEntity();
            // Convert Date to LocalDateTime
            LocalDateTime orderDate = Instant.ofEpochMilli(
                    faker.date().past(30, TimeUnit.DAYS).getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            order.setOrderDate(orderDate);
            order.setOrderStatus("Confirmed");
            
            // Assign a random member to the order
//            Long memberId = Long.valueOf(faker.number().numberBetween(1, numberOfFakeUsers));
            
            int index = i % numberOfFakeUsers;
            if (index == 0) {
                index = numberOfFakeUsers;
            }
            UserEntity member = userRepository.findById(Long.valueOf(index))
                .orElseThrow(() -> new RuntimeException("Member not found"));
            order.setMember(member);
            
            // Save the order to get an ID
            OrderEntity savedOrder = orderRepository.save(order);
            
            // Create a few order items for each order
            for (int j = 0; j < faker.number().numberBetween(1, maxProductItemsPerOrder); j++) {
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrder(savedOrder);
                
                // Assign a random product option variation to the order item
                Long productOptionVariationId = Long.valueOf(
                    faker.number().numberBetween(1, numberOfFakeProductionOptionVariations));
                ProductOptionVariationEntity productOptionVariation = productOptionVariationRepository.findById(
                        productOptionVariationId)
                    .orElseThrow(() -> new RuntimeException("ProductOptionVariation not found"));
                orderItem.setProductOptionVariation(productOptionVariation);
                
                // Save the order item
                orderItemRepository.save(orderItem);
            }
        }
    }
    
    public String sizeTrimmer(String str, int size){
        int len = str.length();
        if(len >= size) {
            return str.substring(len-size, len-1);
        }
        return str;
    }
}
