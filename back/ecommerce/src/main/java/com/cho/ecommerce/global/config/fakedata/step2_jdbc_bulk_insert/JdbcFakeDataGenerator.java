package com.cho.ecommerce.global.config.fakedata.step2_jdbc_bulk_insert;

import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JdbcFakeDataGenerator {
    
    private final Logger log = LoggerFactory.getLogger(JdbcFakeDataGenerator.class);
    private DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    private final Random random = new Random();
    
    private final LocalDateTime startTimeForDiscount;
    private final LocalDateTime endTimeForDiscount;
    private final Timestamp CURRENT_TIMESTAMP = new Timestamp(System.currentTimeMillis());
    
    public void bulkInsert(int numberOfUsers, int numberOfLowCategoriesPerMidCategories,
        int numberOfOptions, int numberOfOptionVariations, int numberOfProducts,
        int numberOfProductItemsPerProduct,
        int numberOfDiscountsPerProductItem,
        int numberOfProductOptionVariationPerProductItem,
        int numberOfOrderItemsPerOrder,
        int batchSize) {
        bulkInsertUsersAndAddresses(numberOfUsers, batchSize);
        bulkInsertCategoriesOptionsAndVariations(numberOfLowCategoriesPerMidCategories,
            numberOfOptions, numberOfOptionVariations, batchSize);
        bulkInsertProductsAndRelated(numberOfProducts,
            numberOfProductItemsPerProduct,
            numberOfDiscountsPerProductItem,
            numberOfProductOptionVariationPerProductItem,
            batchSize);
        bulkInsertOrdersOrderItems(numberOfUsers,
            numberOfOrderItemsPerOrder,
            batchSize);
    }
    
    /**
     * @formatter:off
     * order, orderItems을 bulk-insert 하는 코드
     * @param numberOfUsers
     *  - 유저 수의 N곱 만큼 order를 넣는다.
     * @param numberOfOrderItemsPerOrder
     *  - 주문 수 마다 속하는 N개의 orderItem
     * @param batchSize
     *  - application.yml에
     *  - spring.jpa.properties.hibernate.jdbc.batch_size: 1000
     *  - 의 값과 동일하게 세팅하는걸 권장한다.
     */
    public void bulkInsertOrdersOrderItems(int numberOfUsers,
        int numberOfOrderItemsPerOrder, int batchSize) {
        String orderSql = "INSERT INTO `ORDER` (ORDER_ID, ORDER_DATE, ORDER_STATUS, MEMBER_ID) VALUES (?, ?, ?, ?)";
        String orderItemSql = "INSERT INTO ORDER_ITEM (ORDER_ITEM_ID, QUANTITY, PRICE, ORDER_ID, PRODUCT_OPTION_VARIATION_ID) VALUES (?, ?, ?, ?, ?)";
        
        Connection connection = null;
        PreparedStatement orderStatement = null;
        PreparedStatement orderItemStatement = null;
        
        try {
            connection = dataSource.getConnection();
            orderStatement = connection.prepareStatement(orderSql);
            orderItemStatement = connection.prepareStatement(orderItemSql);
            
            connection.setAutoCommit(false);
            
            Long orderId = 1L;
            Long orderItemId = 1L;
            
            for (int i = 1; i <= numberOfUsers; i++) {
                // Insert orders
//                LocalDateTime orderDate = Instant.ofEpochMilli(
//                        faker.date().past(730, TimeUnit.DAYS).getTime())
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDateTime();
//
//                orderStatement.setLong(1, orderId);
//                orderStatement.setObject(2, orderDate);
//                orderStatement.setString(3, "Confirmed");
//                orderStatement.setLong(4,
//                    orderId); // Member ID range same as orderId range, 1:1 match

                orderStatement.setLong(1, orderId);
                orderStatement.setTimestamp(2, CURRENT_TIMESTAMP);
                orderStatement.setString(3, "Confirmed");
                orderStatement.setLong(4,
                    orderId); // Member ID range same as orderId range, 1:1 match
                
                orderStatement.addBatch();
                orderStatement.clearParameters();
                
                // Insert order items
                for (int j = 1; j <= numberOfOrderItemsPerOrder; j++) {
                    orderItemStatement.setLong(1, orderItemId);
                    orderItemStatement.setInt(2,
                        10); // amount of productItem ordered, warn! need to make sure it's smaller than productItem's quantity
                    orderItemStatement.setDouble(3, 10000); // price of productItem * discounts
                    orderItemStatement.setLong(4, orderId);
                    orderItemStatement.setLong(5,
                        orderItemId);
                    
                    orderItemStatement.addBatch();
                    orderItemStatement.clearParameters();
                    
                    if (j % batchSize == 0) {
                        orderItemStatement.executeBatch();
                        orderItemStatement.clearBatch();
                    }
                    orderItemId++;
                }
                
                if (i % batchSize == 0) {
                    orderStatement.executeBatch();
                    orderStatement.clearBatch();
                }
                orderId++;
            }
            
            // Execute remaining batches
            if (orderId % batchSize != 0) {
                orderStatement.executeBatch();
            }
            if (orderItemId % batchSize != 0) {
                orderItemStatement.executeBatch();
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            log.error("An error occurred during bulk insert:");
            e.printStackTrace();
        } finally {
            // Close the statements and connection in the finally block
            if (orderItemStatement != null) {
                try {
                    orderItemStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (orderStatement != null) {
                try {
                    orderStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @formatter:off
     * product, productItem, discount, productOptionVariation을 bulk-insert 하는 코드
     *
     * @param numberOfProducts
     * @param numberOfProductItemsPerProduct
     *  - 상품당 존재하는 상품 아이템의 수. ex. 독거미 키보드가 Product라면, 일반 모델의 수량/가격/할인, 프로 모델의 수량/가격/할인 등 때문에 정규화한 테이블
     * @param numberOfDiscountsPerProductItem
     *  - 상품 아이템당 적용할 수 있는 할인 쿠폰
     * @param numberOfProductOptionVariationPerProductItem
     *  - 상품 아이템과 상품 옵션을 연관짓기 위한 테이블
     * @param batchSize
     *  - application.yml에
     *  - spring.jpa.properties.hibernate.jdbc.batch_size: 1000
     *  - 의 값과 동일하게 세팅하는걸 권장한다.
     */
    public void bulkInsertProductsAndRelated(int numberOfProducts,
        int numberOfProductItemsPerProduct,
        int numberOfDiscountsPerProductItem,
        int numberOfProductOptionVariationPerProductItem,
        int batchSize) {
        String productSql = "INSERT INTO PRODUCT (PRODUCT_ID, NAME, DESCRIPTION, RATING, RATING_COUNT, CATEGORY_ID) VALUES (?, ?, ?, ?, ?, ?)";
        String productItemSql = "INSERT INTO PRODUCT_ITEM (PRODUCT_ITEM_ID, QUANTITY, PRICE, PRODUCT_ID) VALUES (?, ?, ?, ?)";
        String discountSql = "INSERT INTO DISCOUNT (DISCOUNT_ID, DISCOUNT_TYPE, DISCOUNT_VALUE, START_DATE, END_DATE, PRODUCT_ITEM_ID) VALUES (?, ?, ?, ?, ?, ?)";
        String productOptionVariationSql = "INSERT INTO PRODUCT_OPTION_VARIATION (PRODUCT_OPTION_VARIATION_ID, OPTION_VARIATION_ID, PRODUCT_ITEM_ID) VALUES (?, ?, ?)";
        
        Connection connection = null;
        PreparedStatement productStatement = null;
        PreparedStatement productItemStatement = null;
        PreparedStatement discountStatement = null;
        PreparedStatement productOptionVariationStatement = null;
        
        try {
            connection = dataSource.getConnection();
            productStatement = connection.prepareStatement(productSql);
            productItemStatement = connection.prepareStatement(productItemSql);
            discountStatement = connection.prepareStatement(discountSql);
            productOptionVariationStatement = connection.prepareStatement(
                productOptionVariationSql);
            
            connection.setAutoCommit(false);
            
            Long productId = 1L;
            Long productItemId = 1L;
            Long discountId = 1L;
            Long productOptionVariationId = 1L;
            
            for (int i = 1; i <= numberOfProducts; i++) {
                // Insert products
//                String productName = sizeTrimmer(faker.commerce().productName(),
//                    DatabaseConstants.PRODUCT_NAME_SIZE);
//                String productDescription = sizeTrimmer(faker.lorem().sentence(),
//                    DatabaseConstants.PRODUCT_DESCRIPTION_SIZE);
//                Double productRating = faker.number().randomDouble(1, 1, 5);
//                Integer productRatingCount = faker.number().numberBetween(1, 1000);
//
//                productStatement.setLong(1, productId);
//                productStatement.setString(2, productName);
//                productStatement.setString(3, productDescription);
//                productStatement.setDouble(4, productRating);
//                productStatement.setInt(5, productRatingCount);
//                productStatement.setLong(6, i % 60 + 16); // low level Category ID range: 16 ~ 75
                productStatement.setLong(1, productId);
                productStatement.setString(2, "productName");
                productStatement.setString(3, "productDescription");
                productStatement.setDouble(4, 5.3);
                productStatement.setInt(5, 1000);
                productStatement.setLong(6, i % 60 + 16); // low level Category ID range: 16 ~ 75
                
                productStatement.addBatch();
                productStatement.clearParameters();
                
                // Insert product items
                for (int j = 1; j <= numberOfProductItemsPerProduct; j++) {
//                    Integer productItemQuantity = faker.number().numberBetween(1, 100);
//                    Double productItemPrice = faker.number().randomDouble(2, 1, 10000);
//
//                    productItemStatement.setLong(1, productItemId);
//                    productItemStatement.setInt(2, productItemQuantity);
//                    productItemStatement.setDouble(3, productItemPrice);
//                    productItemStatement.setLong(4, productId);
                    productItemStatement.setLong(1, productItemId);
                    productItemStatement.setInt(2, 30);
                    productItemStatement.setDouble(3, 10000);
                    productItemStatement.setLong(4, productId);
                    
                    productItemStatement.addBatch();
                    productItemStatement.clearParameters();
                    
                    // Insert discounts
                    for (int k = 1; k <= numberOfDiscountsPerProductItem; k++) {
//                        String discountType = DiscountType.values()[faker.number()
//                            .numberBetween(0, DiscountType.values().length)].toString();
//                        Double discountRate = faker.number().randomDouble(2, 1, 100);
//                        OffsetDateTime startTimeForDiscount = faker.date().past(10, TimeUnit.DAYS)
//                            .toInstant()
//                            .atZone(ZoneId.systemDefault())
//                            .toOffsetDateTime();
//                        OffsetDateTime endTimeForDiscount = faker.date().future(10, TimeUnit.DAYS)
//                            .toInstant()
//                            .atZone(ZoneId.systemDefault()).toOffsetDateTime();
//
//                        discountStatement.setLong(1, discountId);
//                        discountStatement.setString(2, discountType);
//                        discountStatement.setDouble(3, discountRate);
//                        discountStatement.setObject(4,
//                            startTimeForDiscount); //START_TIME_FOR_DISCOUNT
//                        discountStatement.setObject(5,
//                            endTimeForDiscount); //END_TIME_FOR_DISCOUNT
//                        discountStatement.setLong(6, productItemId);
                        discountStatement.setLong(1, discountId);
                        discountStatement.setString(2,
                            k % 2 == 1 ? "Percentage" : "Flat Rate");
                        discountStatement.setDouble(3, 40.3);
                        discountStatement.setObject(4,
                            startTimeForDiscount); //START_TIME_FOR_DISCOUNT
                        discountStatement.setObject(5,
                            endTimeForDiscount); //END_TIME_FOR_DISCOUNT
                        discountStatement.setLong(6, productItemId);
                        
                        discountStatement.addBatch();
                        discountStatement.clearParameters();
                        
                        if (discountId % batchSize == 0) {
                            discountStatement.executeBatch();
                            discountStatement.clearBatch();
                        }
                        discountId++;
                    }
                    
                    // Insert product option variations
                    for (int k = 1; k <= numberOfProductOptionVariationPerProductItem; k++) {
                        productOptionVariationStatement.setLong(1, productOptionVariationId);
                        productOptionVariationStatement.setLong(2, random.nextLong(1,
                            541)); // OptionVariation ID range (1~540)
                        productOptionVariationStatement.setLong(3, random.nextLong(1,
                            numberOfProducts * numberOfProductItemsPerProduct
                                + 1)); // ProductItem ID range (1~3000), 3000 = numberOfProducts * numberOfProductItemsPerProduct
                        
                        productOptionVariationStatement.addBatch();
                        productOptionVariationStatement.clearParameters();
                        
                        if (productOptionVariationId % batchSize == 0) {
                            productOptionVariationStatement.executeBatch();
                            productOptionVariationStatement.clearBatch();
                        }
                        productOptionVariationId++;
                    }
                    
                    if (productItemId % batchSize == 0) {
                        productItemStatement.executeBatch();
                        productItemStatement.clearBatch();
                    }
                    productItemId++;
                }
                
                if (productId % batchSize == 0) {
                    productStatement.executeBatch();
                    productStatement.clearBatch();
                }
                productId++;
            }
            
            // Execute remaining batches
            if (productId % batchSize != 0) {
                productStatement.executeBatch();
                productStatement.clearBatch();
            }
            if (productItemId % batchSize != 0) {
                productItemStatement.executeBatch();
                productItemStatement.clearBatch();
            }
            if (discountId % batchSize != 0) {
                discountStatement.executeBatch();
                discountStatement.clearBatch();
            }
            if (productOptionVariationId % batchSize != 0) {
                productOptionVariationStatement.executeBatch();
                productOptionVariationStatement.clearBatch();
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            log.error("An error occurred during bulk insert:");
            e.printStackTrace();
        } finally {
            // Close the statements and connection in the finally block
            if (productOptionVariationStatement != null) {
                try {
                    productOptionVariationStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (discountStatement != null) {
                try {
                    discountStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (productItemStatement != null) {
                try {
                    productItemStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (productStatement != null) {
                try {
                    productStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @formatter:off
     *
     * 유저, 유저권한, 권한, 주소를 유저수와 동일하게 생성하는 bulk-insert code
     *
     * @param numberOfUsers
     * @param batchSize
     *  - application.yml에
     *  - spring.jpa.properties.hibernate.jdbc.batch_size: 1000
     *  - 의 값과 동일하게 세팅하는걸 권장한다.
     */
    public void bulkInsertUsersAndAddresses(int numberOfUsers, int batchSize) {
        String addressSql = "INSERT INTO ADDRESS (ADDRESS_ID, STREET, CITY, STATE, COUNTRY, ZIP_CODE) VALUES (?, ?, ?, ?, ?, ?)";
        String authoritySql = "INSERT INTO AUTHORITY (AUTHORITY_ID, AUTHORITY) VALUES (?, ?)";
        String userSql = "INSERT INTO MEMBER (MEMBER_ID, USER_ID, EMAIL, NAME, ADDRESS_ID, PASSWORD, ROLE, ENABLED, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String userAuthoritySql = "INSERT INTO MEMBER_AUTHORITY (USER_AUTHORITY_ID, MEMBER_ID, AUTHORITY_ID) VALUES (?, ?, ?)";
    
        Connection connection = null;
        PreparedStatement addressStatement = null;
        PreparedStatement authorityStatement = null;
        PreparedStatement userStatement = null;
        PreparedStatement userAuthorityStatement = null;
    
        try {
            connection = dataSource.getConnection();
            addressStatement = connection.prepareStatement(addressSql);
            authorityStatement = connection.prepareStatement(authoritySql);
            userStatement = connection.prepareStatement(userSql);
            userAuthorityStatement = connection.prepareStatement(userAuthoritySql);
    
            connection.setAutoCommit(
                false); //disable auto-commit to perform the bulk insert as a single transaction
            
            // step1) Insert user's addresses
            for (int i = 1; i <= numberOfUsers; i++) {
//                String streetAddress = sizeTrimmer(faker.address().streetAddress(), DatabaseConstants.STREET_SIZE);
//                String city = sizeTrimmer(faker.address().city(), DatabaseConstants.CITY_SIZE);
//                String state = sizeTrimmer(faker.address().state(), DatabaseConstants.STATE_SIZE);
//                String country = sizeTrimmer(faker.address().country(), DatabaseConstants.COUNTRY_SIZE);
//                String zipCode = sizeTrimmer(faker.address().zipCode(), DatabaseConstants.ZIPCODE_SIZE);
//
//                addressStatement.setLong(1, i);
//                addressStatement.setString(2, streetAddress);
//                addressStatement.setString(3, city);
//                addressStatement.setString(4, state);
//                addressStatement.setString(5, country);
//                addressStatement.setString(6, zipCode);
                addressStatement.setLong(1, i);
                addressStatement.setString(2, "street");
                addressStatement.setString(3, "city");
                addressStatement.setString(4, "state");
                addressStatement.setString(5, "country");
                addressStatement.setString(6, "zipcode");
                
                // batch에 추가
                addressStatement.addBatch();
                // batch 메모리에 넣은 후 파라미터 클리어
                addressStatement.clearParameters();
                
                if ((i % batchSize) == 0 || i == numberOfUsers) {
                    addressStatement.executeBatch();
                    addressStatement.clearBatch();
                }
            }
            
            // step2) Insert authorities
            authorityStatement.setLong(1, 1);
            authorityStatement.setString(2, "ROLE_USER");
            authorityStatement.addBatch();
            authorityStatement.clearParameters();
            authorityStatement.setLong(1, 2);
            authorityStatement.setString(2, "ROLE_ADMIN");
            authorityStatement.addBatch();
            authorityStatement.clearParameters();
            
            authorityStatement.executeBatch();
            authorityStatement.clearBatch();
            
            // step3) Insert users
            for (int i = 1; i <= numberOfUsers; i++) {
//                String userName = sizeTrimmer(UUID.randomUUID().toString(),
//                    DatabaseConstants.MEMBER_USERNAME_SIZE); //use UUID to avoid duplicate of userId
//                String name = sizeTrimmer(faker.name().fullName(), DatabaseConstants.MEMBER_NAME_SIZE);
//                String email = sizeTrimmer(faker.internet().emailAddress(),
//                    DatabaseConstants.EMAIL_SIZE);
//                String password = passwordEncoder.encode("password");
//
//                userStatement.setLong(1, i);
//                userStatement.setString(2, userName);
//                userStatement.setString(3, email);
//                userStatement.setString(4, name);
//                userStatement.setLong(5, i);
//                userStatement.setString(6, password);
//                userStatement.setString(7, "ROLE_USER");
//                userStatement.setBoolean(8, true);
//                userStatement.setTimestamp(9, CURRENT_TIMESTAMP);
//                userStatement.setTimestamp(10, CURRENT_TIMESTAMP);
                userStatement.setLong(1, i);
                userStatement.setString(2, "username");
                userStatement.setString(3, "user@gmail.com");
                userStatement.setString(4, "name");
                userStatement.setLong(5, i);
                userStatement.setString(6, "password");
                userStatement.setString(7, "ROLE_USER");
                userStatement.setBoolean(8, true);
                userStatement.setTimestamp(9, CURRENT_TIMESTAMP);
                userStatement.setTimestamp(10, CURRENT_TIMESTAMP);
                
                // batch에 추가
                userStatement.addBatch();
                
                // batch 메모리에 넣은 후 파라미터 클리어
                userStatement.clearParameters();
                
                if ((i % batchSize) == 0 || i == numberOfUsers) {
                    userStatement.executeBatch();
                    userStatement.clearBatch();
                }
            }
            
            // step4) Insert user authorities
            for (int i = 1; i <= numberOfUsers; i++) {
                userAuthorityStatement.setLong(1, i);
                userAuthorityStatement.setLong(2, i);
                userAuthorityStatement.setLong(3, i);
                
                // batch에 추가
                userAuthorityStatement.addBatch();
                
                // batch 메모리에 넣은 후 파라미터 클리어
                userAuthorityStatement.clearParameters();
                
                if ((i % batchSize) == 0 || i == numberOfUsers) {
                    userAuthorityStatement.executeBatch();
                    userAuthorityStatement.clearBatch();
                }
            }
            //step5) commit
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            log.error("An error occurred during bulk insert:");
            e.printStackTrace();
        } finally {
            // Close the statements and connection in the finally block
            if (userAuthorityStatement != null) {
                try {
                    userAuthorityStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (userStatement != null) {
                try {
                    userStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (authorityStatement != null) {
                try {
                    authorityStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (addressStatement != null) {
                try {
                    addressStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @formatter:off
     *
     * category, option, optionVariation을 bulk-insert 하는 코드
     * category는 3 tier로 나누어 insert 한다.
     * 1. top level category (MEN, WOMEN, KIDS) = 3 rows
     * 2. mid level category (Hat, Shirts, Pants, Shoes per each top category) = 12 rows
     * 3. low level category = 60 rows
     * total 75 rows
     *
     * option은 low level category 당 3개씩 만든다.
     * total 60 * 3 = 180 rows
     *
     * optionVariation은 option 당 3개씩 만든다.
     * total 180 * 3 = 540 rows
     *
     * @param numberOfLowCategoriesPerMidCategories
     *  - mid level 카테고리가 총 12개(Men,Women,Kids 에 각각 Hat,Shirts,Pants,Shoes)인데,
     *  - 각 mid level category(12 rows) 마다 생성하고 싶은 low level category
     * @param numberOfOptions
     *  - 각 low level category(60 rows) 당 생성하고 싶은 option의 숫자
     * @param numberOfOptionVariations
     *  - 각 option(180 rows) 당 생성하고 싶은 optionVariation의 숫자
     * @param batchSize
     *  - application.yml에
     *  - spring.jpa.properties.hibernate.jdbc.batch_size: 1000
     *  - 의 값과 동일하게 세팅하는걸 권장한다.
     */
    public void bulkInsertCategoriesOptionsAndVariations(int numberOfLowCategoriesPerMidCategories,
        int numberOfOptions, int numberOfOptionVariations, int batchSize) {
        String categorySql = "INSERT INTO CATEGORY (CATEGORY_ID, CATEGORY_CODE, NAME, PARENT_CATEGORY_ID, DEPTH) VALUES (?, ?, ?, ?, ?)";
        String optionSql = "INSERT INTO `OPTION` (OPTION_ID, VALUE, CATEGORY_ID) VALUES (?, ?, ?)";
        String optionVariationSql = "INSERT INTO OPTION_VARIATION (OPTION_VARIATION_ID, VALUE, OPTION_ID) VALUES (?, ?, ?)";
    
        Connection connection = null;
        PreparedStatement categoryStatement = null;
        PreparedStatement optionStatement = null;
        PreparedStatement optionVariationStatement = null;
    
        try {
            connection = dataSource.getConnection();
            categoryStatement = connection.prepareStatement(categorySql);
            optionStatement = connection.prepareStatement(optionSql);
            optionVariationStatement = connection.prepareStatement(optionVariationSql);
    
            Long categoryIndex = 1L;
            Long optionIndex = 1L;
            Long optionVariationIndex = 1L;
            
            Map<String, Long> topCategories = new LinkedHashMap<>(); //Map을 insert한 순서대로 읽기 위한 자료구조
            topCategories.put("Men", categoryIndex++);
            topCategories.put("Women", categoryIndex++);
            topCategories.put("Kids", categoryIndex++);
            
            Map<String, Long> midCategories = new LinkedHashMap<>(); //Map을 insert한 순서대로 읽기 위한 자료구조
            
            connection.setAutoCommit(false);
            
            // step1) Insert top level categories
            for (Map.Entry<String, Long> topCategoryInfo : topCategories.entrySet()) {
                String topCategoryCode = sizeTrimmer(faker.code().asin(),
                    DatabaseConstants.CATEGORY_CODE_SIZE);
    
                categoryStatement.setLong(1, topCategoryInfo.getValue());
                categoryStatement.setString(2, topCategoryCode);
                categoryStatement.setString(3, topCategoryInfo.getKey());
                categoryStatement.setLong(4, 0);
                categoryStatement.setInt(5, 0);
//                categoryStatement.setLong(1, topCategoryInfo.getValue());
//                categoryStatement.setString(2, "top_category_code");
//                categoryStatement.setString(3, topCategoryInfo.getKey());
//                categoryStatement.setLong(4, 0);
//                categoryStatement.setInt(5, 0);
                
                categoryStatement.addBatch();
                categoryStatement.clearParameters();
                
                midCategories.put(topCategoryInfo.getKey() + "'s Hat", categoryIndex++);
                midCategories.put(topCategoryInfo.getKey() + "'s Top", categoryIndex++);
                midCategories.put(topCategoryInfo.getKey() + "'s Bottom", categoryIndex++);
                midCategories.put(topCategoryInfo.getKey() + "'s Shoes", categoryIndex++);
            }
            
            //step2) add mid level categories
            for (Map.Entry<String, Long> midCategory : midCategories.entrySet()) {
                String midCategoryCode = sizeTrimmer(faker.code().asin(),
                    DatabaseConstants.CATEGORY_CODE_SIZE);
                categoryStatement.setLong(1, midCategory.getValue());
                categoryStatement.setString(2, midCategoryCode);
                categoryStatement.setString(3, midCategory.getKey());
                categoryStatement.setLong(4, midCategory.getValue() / 4); //parent_id
                categoryStatement.setInt(5, 1);
//                categoryStatement.setLong(1, midCategory.getValue());
//                categoryStatement.setString(2, "midCategoryCode");
//                categoryStatement.setString(3, "midCategoryName");
//                categoryStatement.setLong(4, midCategory.getValue() / 4); //parent_id
//                categoryStatement.setInt(5, 1);
                
                categoryStatement.addBatch();
                categoryStatement.clearParameters();
                
                //step3) add low level categories
                for (int i = 0; i < numberOfLowCategoriesPerMidCategories; i++) {
//                    String lowCategoryCode = sizeTrimmer(faker.code().asin(),
//                        DatabaseConstants.CATEGORY_CODE_SIZE);
//                    String lowCategoryName = sizeTrimmer(faker.commerce().department(),
//                        DatabaseConstants.CATEGORY_NAME_SIZE);
//
//                    categoryStatement.setLong(1, categoryIndex++);
//                    categoryStatement.setString(2, lowCategoryCode);
//                    categoryStatement.setString(3, lowCategoryName);
//                    categoryStatement.setLong(4, midCategory.getValue()); //parent_id
//                    categoryStatement.setInt(5, 2);
                    
                    categoryStatement.setLong(1, categoryIndex++);
                    categoryStatement.setString(2, "low_category_code");
                    categoryStatement.setString(3, "low_category_name");
                    categoryStatement.setLong(4, midCategory.getValue()); //parent_id
                    categoryStatement.setInt(5, 2);
                    
                    categoryStatement.addBatch();
                    categoryStatement.clearParameters();
                    
                    // step4) Insert options
                    for (int j = 1; j <= numberOfOptions; j++) {
//                        String optionValue = sizeTrimmer(faker.commerce().material(),
//                            DatabaseConstants.OPTION_VALUE_SIZE);
//
//                        optionStatement.setLong(1, optionIndex++);
//                        optionStatement.setString(2, optionValue);
//                        optionStatement.setLong(3,
//                            categoryIndex - 1); //category id
                        
                        optionStatement.setLong(1, optionIndex++);
                        optionStatement.setString(2, "OptionName");
                        optionStatement.setLong(3,
                            categoryIndex - 1); //category id
                        
                        optionStatement.addBatch();
                        optionStatement.clearParameters();
                        
                        //step5) Insert option variations
                        for (int p = 1; p <= numberOfOptionVariations; p++) {
//                            String optionVariationValue = sizeTrimmer(faker.color().name(),
//                                DatabaseConstants.OPTION_VARIATION_VALUE_SIZE);
//
//                            optionVariationStatement.setLong(1, optionVariationIndex++);
//                            optionVariationStatement.setString(2, optionVariationValue);
//                            optionVariationStatement.setLong(3,
//                                optionIndex - 1); //option id
                            optionVariationStatement.setLong(1, optionVariationIndex++);
                            optionVariationStatement.setString(2, "OptionVariationName");
                            optionVariationStatement.setLong(3,
                                optionIndex - 1); //option id
                            
                            optionVariationStatement.addBatch();
                            optionVariationStatement.clearParameters();
                        }
                    }
                }
            }
            
            categoryStatement.executeBatch();
            categoryStatement.clearBatch();
            
            optionStatement.executeBatch();
            optionStatement.clearBatch();
            
            optionVariationStatement.executeBatch();
            optionVariationStatement.clearBatch();
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            log.error("An error occurred during bulk insert:");
            e.printStackTrace();
        } finally {
            // Close the statements and connection in the finally block
            if (optionVariationStatement != null) {
                try {
                    optionVariationStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (optionStatement != null) {
                try {
                    optionStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (categoryStatement != null) {
                try {
                    categoryStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public String sizeTrimmer(String str, int size) {
        int len = str.length();
        if (len >= size) {
            return str.substring(len - size, len - 1);
        }
        return str;
    }
}
