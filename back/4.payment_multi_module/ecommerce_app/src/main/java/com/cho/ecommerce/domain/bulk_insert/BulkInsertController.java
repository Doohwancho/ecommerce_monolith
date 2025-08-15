package com.cho.ecommerce.domain.bulk_insert;

import com.cho.ecommerce.global.config.fakedata.JdbcFakeDataGenerator;
import com.cho.ecommerce.global.config.util.DatabaseCleanup;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> bulkInsert(@PathVariable("amount") String amount) {
        try {
            log.info("Clearing database before bulk-insert");
            databaseCleanup.afterPropertiesSet();
            databaseCleanup.execute();
            
            log.info("Bulk-insert start!");
            long startTime = System.currentTimeMillis();
            
            int baseAmount = Integer.parseInt(amount);
            int batchSize = 1000;
            
            // ecommerce app user:product:order ratio
            // user : product : order = 1 : 10 : 5
            jdbcDataGenerator.bulkInsert(baseAmount, 10 * baseAmount, 5 * baseAmount, batchSize);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("Total execution time: " + duration + " ms");
            return ResponseEntity.ok("Bulk-insert completed successfully!");
        } catch (Exception e) {
            log.error("Error during bulk insert", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Bulk-insert failed: " + e.getMessage());
        }
    }
}
