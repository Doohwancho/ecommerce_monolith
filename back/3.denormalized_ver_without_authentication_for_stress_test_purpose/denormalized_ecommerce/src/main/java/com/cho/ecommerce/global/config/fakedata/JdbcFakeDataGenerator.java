package com.cho.ecommerce.global.config.fakedata;

import com.cho.ecommerce.domain.order.domain.Order;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.domain.Product.DiscountDTO;
import com.cho.ecommerce.global.config.parser.ObjectMapperUtil;
import com.cho.ecommerce.global.config.util.RandomValueGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JdbcFakeDataGenerator {
    
    private final Logger log = LoggerFactory.getLogger(JdbcFakeDataGenerator.class);
    private final RandomValueGenerator randomValueGenerator;
    private Integer NUMBER_OF_UNIQUE_STRINGS = 80_000; //520_807
    private static final Integer LENGTH_OF_STRING_FOR_UNIQUE_STRINGS = 10;
    //    private static final int NUMBER_OF_UNIQUE_INTEGER_ONE_TO_THIRTY = 0;
//    private static final int NUMBER_OF_UNIQUE_INTEGER_ONE_TO_THOUSAND = 0;
//    private static final int NUMBER_OF_DOUBLE_ZERO_TO_FIVE = 50;
//    private static final int NUMBER_OF_DOUBLE_ONE_TO_HUNDRED = 1_000;
    private int NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
    private int NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = 10_000;
    private int NUMBER_OF_OPTIONS = 1000;
    private int NUMBER_OF_DISCOUNTS = 1000;
    private static final int NUMBER_OF_DATE_3MONTH_FROM_TODAY = 90;
    public static final int DAYS_FROM_TODAY = 90;
    private DataSource dataSource;
    //    private final Faker faker = new Faker();
//    private final Random random = new Random();
    private String[] uniqueStrings;
    //    private int[] uniqueIntegersOneToThirty;
//    private int[] uniqueIntegersOneToThousand;
    private double[] uniqueDoublesZeroToFive;
    private double[] uniqueDoublesOneToHundred;
    private double[] uniqueDoublesHundredToHundredThousand;
    private double[] uniqueDoublesHundredToMillion;
    private List<String> uniqueOptionJsons;
    private List<String> uniqueDiscountInJsonFormat;
    private List<List<DiscountDTO>> uniqueDiscountInDiscountDTOFormat;
    private LocalDateTime[] uniqueLocalDateTimeThreeMonthsPastToToday;
    private final ZoneOffset offset = ZoneOffset.UTC;
    private final OffsetDateTime nowInOffsetDateTimeFormat = OffsetDateTime.now();
    
    public JdbcFakeDataGenerator(DataSource dataSource,
        RandomValueGenerator randomValueGenerator) {
        this.dataSource = dataSource;
        this.randomValueGenerator = randomValueGenerator;
    }
    
    public void bulkInsert(int numberOfUsers, int numberOfProducts, int numberOfOrders,
        int batchSize)
        throws SQLException, JsonProcessingException, InterruptedException, ExecutionException {
        
        //step1) bulk-insert 전, 얼마나 많은 양의 fake-data를 만들건지 정하기 based on requested input
        int baseAmount = numberOfUsers;
    
        if (baseAmount <= 1000) {
            NUMBER_OF_UNIQUE_STRINGS = 542;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 500;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = baseAmount * 10; //가격의 종류도 amountOfProducts와 비례하도록 설정
            NUMBER_OF_OPTIONS = NUMBER_OF_UNIQUE_STRINGS;
            NUMBER_OF_DISCOUNTS = NUMBER_OF_UNIQUE_STRINGS;
        } else if (baseAmount <= 10000) {
            NUMBER_OF_UNIQUE_STRINGS = 10_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = baseAmount * 10; //가격의 종류도 amountOfProducts와 비례하도록 설정
            NUMBER_OF_OPTIONS = NUMBER_OF_UNIQUE_STRINGS;
            NUMBER_OF_DISCOUNTS = NUMBER_OF_UNIQUE_STRINGS;
        } else if (baseAmount <= 100_000) {
            NUMBER_OF_UNIQUE_STRINGS = baseAmount;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = 10_000; //가격의 갯수는 최대 10000종류로 고정
            NUMBER_OF_OPTIONS = NUMBER_OF_UNIQUE_STRINGS;
            NUMBER_OF_DISCOUNTS = NUMBER_OF_UNIQUE_STRINGS;
        } else {
            NUMBER_OF_UNIQUE_STRINGS = 100_000; //strings를 너무 많이 만들면 RAM 부족함. 최대 10만개로 고정.
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = 10_000; //가격의 갯수는 최대 10000종류로 고정
            NUMBER_OF_OPTIONS = NUMBER_OF_UNIQUE_STRINGS;
            NUMBER_OF_DISCOUNTS = NUMBER_OF_UNIQUE_STRINGS;
        }
    
        // step2) Generate fake data(ex. unique strings, doubles, etc)
        this.uniqueStrings = randomValueGenerator.generateUniqueStrings(NUMBER_OF_UNIQUE_STRINGS,
            LENGTH_OF_STRING_FOR_UNIQUE_STRINGS);
        this.uniqueDoublesZeroToFive = randomValueGenerator.generateRandomDoublesByPointOne(0, 5);
        this.uniqueDoublesOneToHundred = randomValueGenerator.generateRandomDoublesByPointOne(1,
            100);
        this.uniqueDoublesHundredToHundredThousand = randomValueGenerator.generateRandomDoubles(
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND, 100, 100000);
        this.uniqueDoublesHundredToMillion = randomValueGenerator.generateRandomDoubles(
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION, 100,
            1000000);
        this.uniqueLocalDateTimeThreeMonthsPastToToday = randomValueGenerator.generateRandomDates(
            NUMBER_OF_DATE_3MONTH_FROM_TODAY, DAYS_FROM_TODAY);
        this.uniqueOptionJsons = randomValueGenerator.generateRandomOptionsList(NUMBER_OF_OPTIONS);
        List[] randomDiscounts = randomValueGenerator.generateRandomDiscountsInJsonFormatAndDiscountDTOFormat(
            NUMBER_OF_DISCOUNTS, uniqueDoublesOneToHundred,
            uniqueLocalDateTimeThreeMonthsPastToToday);
        this.uniqueDiscountInJsonFormat = randomDiscounts[0];
        this.uniqueDiscountInDiscountDTOFormat = randomDiscounts[1];
    
        
        //step3) cpu core 수 만큼 threadpool에 threads를 만들고, 전체 bulk-insert할 양을 1/n 해서 쓰레드한테 각자 할당 후, 동시에 bulk-insert 실행
        int numThreads = Runtime.getRuntime().availableProcessors(); //cpu core 수 만큼 bulk-insert를 분할정복할 thread 생성
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); //bulk-insert를 불할정복할 thread pool 생성
    
        List<Future<?>> futures = new ArrayList<>();
    
        for (int i = 0; i < numThreads; i++) {
            int startUser = i * (numberOfUsers / numThreads);
            int endUser = (i == numThreads - 1) ? numberOfUsers : (i + 1) * (numberOfUsers / numThreads);
        
            int startProduct = i * (numberOfProducts / numThreads);
            int endProduct = (i == numThreads - 1) ? numberOfProducts : (i + 1) * (numberOfProducts / numThreads);
        
            int startOrder = i * (numberOfOrders / numThreads);
            int endOrder = (i == numThreads - 1) ? numberOfOrders : (i + 1) * (numberOfOrders / numThreads);
        
            futures.add(executorService.submit(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    connection.setAutoCommit(false);
                    bulkInsertDenormalizedUsers(connection, startUser, endUser, batchSize);
                    bulkInsertDenormalizedProducts(connection, startProduct, endProduct, batchSize);
                    bulkInsertDenormalizedOrders(connection, startOrder, endOrder, numberOfUsers, numberOfProducts, batchSize);
                    connection.commit();
                } catch (SQLException | JsonProcessingException e) {
                    log.error("Error in bulk insert thread", e);
                    throw new RuntimeException(e);
                }
            }));
        }
    
        // Wait for all threads to complete
        for (Future<?> future : futures) {
            future.get();
        }
    
        executorService.shutdown();
    }
    
    
    public void bulkInsertDenormalizedProducts(Connection connection, int start, int end, int batchSize) throws SQLException, JsonProcessingException {
        String sql = "INSERT INTO DENORMALIZED_PRODUCT (PRODUCT_ID, NAME, DESCRIPTION, RATING, RATING_COUNT, CATEGORY_ID, CATEGORY_NAME, TOTAL_QUANTITY, OPTIONS, DISCOUNTS, HAS_DISCOUNT, BASE_PRICE, LOWEST_PRICE, HIGHEST_PRICE, LATEST_DISCOUNT_START, LATEST_DISCOUNT_END) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        
            connection.setAutoCommit(false);
        
            for (int i = start; i < end; i++) { //i = productId
                int id = i + 1; // ID starts from 1
                long categoryId = i % 60 + 16;
            
                //PRODUCT_ID
                pstmt.setLong(1, id);
                //NAME
                pstmt.setString(2, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                //DESCRIPTION
                pstmt.setString(3, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                //RATING
                pstmt.setDouble(4, uniqueDoublesZeroToFive[i % 50]);
                //RATING_COUNT
                pstmt.setInt(5, 100);
                //CATEGORY_ID
                pstmt.setDouble(6,
                    categoryId); //16~75 까지 입력(원래 back/1.ecommerce에서 카테고리가 nested_category 식이라, 최상위 카테고리 1~3, 중간단계 카테고리 4~15, 하위단계 카테고리 16~75라서 그렇다.
                //CATEGORY_NAME,
                pstmt.setString(7, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                //TOTAL_QUANTITY,
                pstmt.setInt(8, 100000);
                //OPTIONS,
                pstmt.setString(9, getObjectMapper().writeValueAsString(
                    uniqueOptionJsons.get(i % NUMBER_OF_OPTIONS)));
                // DISCOUNTS
                pstmt.setString(10, getObjectMapper().writeValueAsString(
                    uniqueDiscountInJsonFormat.get(i % NUMBER_OF_DISCOUNTS)));
                //HAS_DISCOUNT,
                pstmt.setBoolean(11, true);
//                boolean hasDiscount = !discounts.isEmpty();
//                pstmt.setBoolean(11, hasDiscount);
                
                //BASE_PRICE,
                Double basePrice = uniqueDoublesHundredToMillion[i
                    % NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION];
                pstmt.setDouble(12, basePrice);
                
                //LOWEST_PRICE
                List<DiscountDTO> discountsList = uniqueDiscountInDiscountDTOFormat.get(
                    i % NUMBER_OF_DISCOUNTS);
                double lowestPrice = calculateLowestPrice(basePrice, discountsList);
                pstmt.setDouble(13, lowestPrice);
                
                //HIGHEST_PRICE,
                pstmt.setDouble(14, basePrice);
                
                // LATEST_DISCOUNT_START
                OffsetDateTime latestStart = discountsList.isEmpty() ? null :
                    discountsList.stream().map(d -> d.getStartDate()).max(OffsetDateTime::compareTo)
                        .orElse(null);
                pstmt.setObject(15, latestStart);
            
                // LATEST_DISCOUNT_END
                OffsetDateTime latestEnd = discountsList.isEmpty() ? null :
                    discountsList.stream().map(d -> d.getEndDate()).max(OffsetDateTime::compareTo)
                        .orElse(null);
                pstmt.setObject(16, latestEnd);
            
                pstmt.addBatch();
                pstmt.clearParameters();
    
                if ((i - start + 1) % batchSize == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
        
            // Execute remaining batches - TODO - is there better way? cuz sometimes this step could be unnecessary
            pstmt.executeBatch(); // Insert remaining records
            pstmt.clearBatch();
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            connection.rollback();
            log.error("An error occurred during bulk insert:");
            e.printStackTrace();
            throw e;
        }
    }
    
    private double calculateLowestPrice(double basePrice, List<DiscountDTO> discounts) {
        double discountedPrice = basePrice;
        for (DiscountDTO discount : discounts) {
            if ("PERCENTAGE".equals(discount.getType())) {
                double discountAmount = discountedPrice * (discount.getValue() / 100);
                discountedPrice -= discountAmount;
            } else if ("FLAT_RATE".equals(discount.getType())) {
                discountedPrice -= discount.getValue();
            }
        }
        return Math.max(discountedPrice, 0);
    }
    
//    public void bulkInsertDenormalizedUsers(Connection connection, int numberOfUsers, int batchSize)
    public void bulkInsertDenormalizedUsers(Connection connection, int start, int end, int batchSize)
        throws SQLException {
        String sql = "INSERT INTO DENORMALIZED_MEMBER (MEMBER_ID, USER_ID, EMAIL, NAME, PASSWORD, ROLE, ENABLED, FAILED_ATTEMPT, STREET, CITY, STATE, COUNTRY, ZIP_CODE, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            
            for (int i = start; i < end; i++) {
                int id = i + 1; // ID starts from 1
    
                // MEMBER_ID
                pstmt.setLong(1, id);
                // USER_ID
                pstmt.setString(2, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // EMAIL
//                pstmt.setString(3, "user" + i + "@example.com");
                pstmt.setString(3, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // NAME
                pstmt.setString(4, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // PASSWORD (hashed)
                pstmt.setString(5, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
//                pstmt.setString(5, "$2a$10$" + uniqueStrings[(i + 1) % NUMBER_OF_UNIQUE_STRINGS]);
                // ROLE
                pstmt.setString(6, "ROLE_USER");
                // ENABLED
                pstmt.setBoolean(7, true);
                // FAILED_ATTEMPT
                pstmt.setInt(8, 0);
                // STREET
                pstmt.setString(9, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // CITY
                pstmt.setString(10, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // STATE
                pstmt.setString(11, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // COUNTRY
                pstmt.setString(12, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // ZIP_CODE
                pstmt.setString(13, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                // CREATED_AT
//                OffsetDateTime createdAt = OffsetDateTime.now().minusDays(i % 365);
                pstmt.setObject(14, nowInOffsetDateTimeFormat);
                // UPDATED_AT
//                OffsetDateTime updatedAt = createdAt.plusDays(i % 30);
                pstmt.setObject(15, nowInOffsetDateTimeFormat);
                
                pstmt.addBatch();
                pstmt.clearParameters();
                
                if ((i - start + 1) % batchSize == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            
            // Execute remaining batches
            pstmt.executeBatch();
            pstmt.clearBatch();
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            log.error("An error occurred during bulk insert of users:", e);
            throw e;
        }
    }
    
    public void bulkInsertDenormalizedOrders(Connection connection, int start, int end, int numberOfUsers, int numberOfProducts, int batchSize) throws SQLException, JsonProcessingException {
        String sql = "INSERT INTO DENORMALIZED_ORDER (ORDER_ID, ORDER_DATE, ORDER_STATUS, MEMBER_ID, MEMBER_NAME, MEMBER_EMAIL, TOTAL_PRICE, TOTAL_QUANTITY, ORDER_ITEMS, STREET, CITY, STATE, COUNTRY, ZIP_CODE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] statuses = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            
            for (int i = start; i < end; i++) {
                int id = i + 1; // ID starts from 1
                int memberId = ((id - 1) % numberOfUsers) + 1;
                int productId = ((id - 1) % numberOfProducts) + 1;
                
                // ORDER_ID
                pstmt.setLong(1, id);
                
                // ORDER_DATE
                OffsetDateTime orderDate = uniqueLocalDateTimeThreeMonthsPastToToday[i % NUMBER_OF_DATE_3MONTH_FROM_TODAY].atOffset(offset);
                pstmt.setObject(2, orderDate);
                
                // ORDER_STATUS
                pstmt.setString(3, statuses[i % 5]);
                
                // MEMBER_ID
                // 수동으로 memberId = orderId를 맞춘다.
                pstmt.setLong(4, memberId);
                
                // MEMBER_NAME
                // 수동으로 member name 을 uniqueStrings 순서대로 써서 맞춘다.
                pstmt.setString(5, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]);
                
                // MEMBER_EMAIL
                // 수동으로 member email을 uniqueStrings 순서대로 써서 맞춘다.
                pstmt.setString(6, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]);
                
                // Generate List<Order.OrderItem> based on List<DiscountDTO>
                List<Order.OrderItem> orderItems = generateOrderItems(productId);
    
                // TOTAL_PRICE based on List<DiscountDTO>
                double totalPrice = orderItems.stream().mapToDouble(item -> item.getDiscountedPrice() * item.getQuantity()).sum();
                pstmt.setDouble(7, totalPrice);
                
                // TOTAL_QUANTITY
                int totalQuantity = orderItems.stream().mapToInt(Order.OrderItem::getQuantity).sum();
                pstmt.setInt(8, totalQuantity);
                
                // ORDER_ITEMS
                pstmt.setString(9, getObjectMapper().writeValueAsString(orderItems));
                
                // Address fields
                pstmt.setString(10, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]); // STREET
                pstmt.setString(11, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]); // CITY
                pstmt.setString(12, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]); // STATE
                pstmt.setString(13, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]); // COUNTRY
                pstmt.setString(14, uniqueStrings[memberId % NUMBER_OF_UNIQUE_STRINGS]); // ZIP_CODE
                
                pstmt.addBatch();
                pstmt.clearParameters();
                
                if ((i - start + 1) % batchSize == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            
            // Execute remaining batches
            pstmt.executeBatch();
            pstmt.clearBatch();
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            log.error("An error occurred during bulk insert of orders:", e);
            throw e;        }
    }
    private List<Order.OrderItem> generateOrderItems(int productId) {
        List<Order.OrderItem> orderItems = new ArrayList<>();
        Order.OrderItem item1 = new Order.OrderItem();
        Order.OrderItem item2 = new Order.OrderItem();
        
        //OrderItem1
        item1.setProductName(uniqueStrings[productId % NUMBER_OF_UNIQUE_STRINGS]);
        item1.setQuantity(1); // Fixed quantity of 1 for simplicity
    
        double basePrice1 = uniqueDoublesHundredToMillion[productId % NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION];
        item1.setBasePrice(basePrice1);
        
        List<Product.DiscountDTO> discountsForItem1 = uniqueDiscountInDiscountDTOFormat.get(productId % NUMBER_OF_DISCOUNTS);
        double discountedPrice1 = calculateLowestPrice(basePrice1, discountsForItem1);
        item1.setDiscountedPrice(discountedPrice1);
    
        //OrderItem2
        item2.setProductName(uniqueStrings[productId * 2 % NUMBER_OF_UNIQUE_STRINGS]);
        item2.setQuantity(1); // Fixed quantity of 1 for simplicity
    
        double basePrice2 = uniqueDoublesHundredToMillion[productId * 2 % NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION];
        item2.setBasePrice(basePrice2);
    
        List<Product.DiscountDTO> discountsForItem2 = uniqueDiscountInDiscountDTOFormat.get(productId * 2 % NUMBER_OF_DISCOUNTS);
        double discountedPrice2 = calculateLowestPrice(basePrice2, discountsForItem2);
        item2.setDiscountedPrice(discountedPrice2);
        
        //add OrderItem1, OrderItem2
        orderItems.add(item1);
        orderItems.add(item2);
        
        return orderItems;
    }
    
    private ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getObjectMapper();
    }
}
