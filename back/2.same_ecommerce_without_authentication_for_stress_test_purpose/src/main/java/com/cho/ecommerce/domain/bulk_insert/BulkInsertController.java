package com.cho.ecommerce.domain.bulk_insert;

import com.cho.ecommerce.global.config.fakedata.step2_jdbc_bulk_insert.JdbcFakeDataGenerator;
import com.cho.ecommerce.global.util.DatabaseCleanup;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/*
    mysql commandline to drop all tables manually that belongs to 'ecommerce' database
    
    SELECT GROUP_CONCAT(CONCAT('`', table_name, '`'))
    INTO @tables
    FROM information_schema.tables
    WHERE table_schema = 'ecommerce';
    SET @query = CONCAT('DROP TABLE IF EXISTS ', @tables);
    PREPARE stmt FROM @query;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SET FOREIGN_KEY_CHECKS = 1;
 */


@Slf4j
@AllArgsConstructor
@RestController
public class BulkInsertController {
    private final JdbcFakeDataGenerator jdbcDataGenerator;
    private final DatabaseCleanup databaseCleanup;
    
    
    @GetMapping("/bulkinsert/{amount}")
    public ResponseEntity<String> bulkInsert(@PathVariable("amount") String amount)
        throws SQLException {
    
        log.info("clear database before bulk-insert");
        databaseCleanup.afterPropertiesSet();
        databaseCleanup.execute();
    
        log.info("Bulk-insert start!");
        long startTime = System.currentTimeMillis();
        
        int baseAmount = Integer.parseInt(amount);
        int batchSize = 1000;
        
        //크기 고정
        int numberOfLowCategoriesPerMidCategories = 5; //75 (3 + (3*4) + (3*4*5) = 3 + 12 + 60
        int numberOfOptions = 3; //180 (60 from low categories * 3)
        int numberOfOptionVariations = 3; //540 (180 from options * 3)
        
        //크기가 유동적으로 바뀜
        int numberOfUsers = baseAmount; //base_number * 4 만큼 총 rows수 늘어남 (user, address, user_authority, order 수 결정)
        int numberOfOrderItemsPerOrder = 2; //number of orders * 2
        int numberOfProducts = baseAmount;
        int numberOfProductItemsPerProduct = 3; //base_number * 3
        int numberOfDiscountsPerProductItem = 1; //1:1 ratio, productItem : discount
        int numberOfProductOptionVariationPerProductItem = 1; //1:1 ratio, productItem : productOptionVariation
        
        //1) if baseAmount is 1000? -> total: 16000 rows
        //users: 1000
        //address: 1000
        //user_authority: 1000
        //orders: 1000
        //orderItems: 2000
        //products: 1000
        //productItems: 3000
        //discoutns: 3000
        //productOptionVariation: 3000
        
        //2) query test용 소규모 db size: 100,000 ~ 500,000
        //if baseAmount is 100,000? -> total: 1,600,000
        //users: 100,000
        //address: 100,000
        //user_authority: 100,000
        //orders: 100,000
        //orderItems: 200,000
        //products: 100,000
        //productItems: 300,000
        //discoutns: 300,000
        //productOptionVariation: 300,000
    
        //3) query test용 중규모 db size: 1,000,000 ~ 5,000,000
        //if baseAmount is 1,000,000? -> total: 16,000,000
        //users: 1,000,000
        //address: 1,000,000
        //user_authority: 1,000,000
        //orders: 1,000,000
        //orderItems: 2,000,000
        //products: 1,000,000
        //productItems: 3,000,000
        //discoutns: 3,000,000
        //productOptionVariation: 3,000,000
        
        jdbcDataGenerator.bulkInsert(numberOfUsers, numberOfLowCategoriesPerMidCategories,
            numberOfOptions, numberOfOptionVariations, numberOfProducts,
            numberOfProductItemsPerProduct,
            numberOfDiscountsPerProductItem,
            numberOfProductOptionVariationPerProductItem,
            numberOfOrderItemsPerOrder,
            batchSize);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("Total execution time: " + duration + " ms");
        return ResponseEntity.ok("Bulk-insert completed successfully!");
    }
}
