package com.cho.ecommerce.global.config.fakedata;

import com.cho.ecommerce.domain.product.domain.Product.DiscountDTO;
import com.cho.ecommerce.global.config.util.RandomValueGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JdbcFakeDataGenerator {
    private final Logger log = LoggerFactory.getLogger(JdbcFakeDataGenerator.class);
    private final ObjectMapper objectMapper;
    private final RandomValueGenerator randomValueGenerator;
    
    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
    private static final int NUM_THREADS = Math.min(NUM_CORES, 4);
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
    private LocalDateTime startTimeForDiscount;
    private LocalDateTime endTimeForDiscount;
    private final Timestamp CURRENT_TIMESTAMP = new Timestamp(System.currentTimeMillis());
    
    public JdbcFakeDataGenerator(DataSource dataSource,
        RandomValueGenerator randomValueGenerator,
        ObjectMapper objectMapper,
        @Qualifier("startTimeForDiscount") LocalDateTime startTimeForDiscount,
        @Qualifier("endTimeForDiscount") LocalDateTime endTimeForDiscount) throws SQLException {
        this.dataSource = dataSource;
        this.randomValueGenerator = randomValueGenerator;
        this.objectMapper = objectMapper;
        this.startTimeForDiscount = startTimeForDiscount;
        this.endTimeForDiscount = endTimeForDiscount;
    }
    
    public void bulkInsert(int numberOfUsers, int numberOfProducts, int numberOfOrders, int batchSize)
        throws SQLException, JsonProcessingException {
        
        int baseAmount = numberOfUsers;
    
        // set size of fake string/float objects in proportion to bulk-insert size
        if(baseAmount <= 1000) {
            NUMBER_OF_UNIQUE_STRINGS = 542;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 500;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = baseAmount;
            NUMBER_OF_OPTIONS = NUMBER_OF_UNIQUE_STRINGS;
            NUMBER_OF_DISCOUNTS = NUMBER_OF_UNIQUE_STRINGS;
        }
        else if(baseAmount <= 10000) {
            NUMBER_OF_UNIQUE_STRINGS = 10_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = baseAmount;
        } else if(baseAmount <= 100_000) {
            NUMBER_OF_UNIQUE_STRINGS = 30_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = 10_000;
        } else {
            NUMBER_OF_UNIQUE_STRINGS = 80_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_HUNDREDTHOUSAND = 1_000;
            NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION = 10_000;
        }
        
        
        // Generate unique strings
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
        List[] randomDiscounts = randomValueGenerator.generateRandomDiscountsInJsonFormatAndDiscountDTOFormat(NUMBER_OF_DISCOUNTS, uniqueDoublesOneToHundred, uniqueLocalDateTimeThreeMonthsPastToToday);
        this.uniqueDiscountInJsonFormat = randomDiscounts[0];
        this.uniqueDiscountInDiscountDTOFormat = randomDiscounts[1];
    
        List<Connection> connectionPool = new ArrayList<>();
    
        
//        if(NUM_CORES == 1) {
            try (Connection connection = dataSource.getConnection();) {
                bulkInsertDenormalizedProducts(connection, numberOfProducts, batchSize);
            } catch (SQLException e) {
                log.error("An error occurred during bulk insert:", e);
                throw e;
            }
//        }
    }
        
    public void bulkInsertDenormalizedProducts(Connection connection, int numberOfProducts, int batchSize) throws SQLException, JsonProcessingException {
        String sql = "INSERT INTO DENORMALIZED_PRODUCT (PRODUCT_ID, NAME, DESCRIPTION, RATING, RATING_COUNT, CATEGORY_ID, CATEGORY_NAME, TOTAL_QUANTITY, OPTIONS, DISCOUNTS, HAS_DISCOUNT, BASE_PRICE, LOWEST_PRICE, HIGHEST_PRICE, LATEST_DISCOUNT_START, LATEST_DISCOUNT_END) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);
    
            for (int i = 1; i <= numberOfProducts; i++) { //i = productId
                long categoryId = i % 60 + 16;
    
                //PRODUCT_ID
                pstmt.setLong(1, i);
                //NAME
                pstmt.setString(2, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                //DESCRIPTION
                pstmt.setString(3, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                //RATING
                pstmt.setDouble(4, uniqueDoublesZeroToFive[i % 50]);
                //RATING_COUNT
                pstmt.setInt(5, 100);
                //CATEGORY_ID
                pstmt.setDouble(6, categoryId); //16~75 까지 입력(원래 back/1.ecommerce에서 카테고리가 nested_category 식이라, 최상위 카테고리 1~3, 중간단계 카테고리 4~15, 하위단계 카테고리 16~75라서 그렇다.
                //CATEGORY_NAME,
                pstmt.setString(7, uniqueStrings[i % NUMBER_OF_UNIQUE_STRINGS]);
                //TOTAL_QUANTITY,
                pstmt.setInt(8, 100000);
                //OPTIONS,
                //? how to insert json type?
                // Generate and set OPTIONS JSON
                pstmt.setString(9, objectMapper.writeValueAsString(uniqueOptionJsons.get(i % NUMBER_OF_OPTIONS)));
    
                // DISCOUNTS
                //? how to insert json type?
                pstmt.setString(10, objectMapper.writeValueAsString(uniqueDiscountInJsonFormat.get(i % NUMBER_OF_DISCOUNTS)));
                
                //HAS_DISCOUNT,
                pstmt.setBoolean(11, true);
//                boolean hasDiscount = !discounts.isEmpty();
//                pstmt.setBoolean(11, hasDiscount);
                
                //BASE_PRICE,
                Double basePrice = uniqueDoublesHundredToMillion[i % NUMBER_OF_DOUBLE_HUNDRED_TO_MILLION];
                pstmt.setDouble(12, basePrice);
                
                //LOWEST_PRICE
                List<DiscountDTO> discountsList = uniqueDiscountInDiscountDTOFormat.get(i % NUMBER_OF_DISCOUNTS);
                double lowestPrice = calculateLowestPrice(basePrice, discountsList);
                pstmt.setDouble(13, lowestPrice);
                //HIGHEST_PRICE,
                pstmt.setDouble(14, basePrice);
                // LATEST_DISCOUNT_START
                OffsetDateTime latestStart = discountsList.isEmpty() ? null :
                    discountsList.stream().map(d -> OffsetDateTime.parse(d.getStartDate())).max(OffsetDateTime::compareTo).orElse(null);
                pstmt.setObject(15, latestStart);
    
                // LATEST_DISCOUNT_END
                OffsetDateTime latestEnd = discountsList.isEmpty() ? null :
                    discountsList.stream().map(d -> OffsetDateTime.parse(d.getEndDate())).max(OffsetDateTime::compareTo).orElse(null);
                pstmt.setObject(16, latestEnd);
    
                pstmt.addBatch();
                pstmt.clearParameters();
    
                if (i % batchSize == 0) {
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
            log.error("An error occurred during bulk insert:");
            e.printStackTrace();
        }
    }
    
    private double calculateLowestPrice(double basePrice, List<DiscountDTO> discounts) {
        double lowestPrice = basePrice;
        for (DiscountDTO discount : discounts) {
            //its always "PERCENTAGE" type
            lowestPrice = Math.min(lowestPrice, basePrice * (1 - discount.getValue() / 100));
//            if ("PERCENTAGE".equals(discount.getType())) {
//                lowestPrice = Math.min(lowestPrice, basePrice * (1 - discount.getValue() / 100));
//            } else if ("FLAT_RATE".equals(discount.getType())) {
//                lowestPrice = Math.min(lowestPrice, basePrice - discount.getValue());
//            }
        }
        return Math.max(lowestPrice, 0);
    }
}
