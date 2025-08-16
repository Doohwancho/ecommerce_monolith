package com.ecommerce.monolith;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/* mysql command to find all index

 SELECT
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE,
    SEQ_IN_INDEX,
    INDEX_TYPE,
    CARDINALITY
FROM
    INFORMATION_SCHEMA.STATISTICS
WHERE
    TABLE_SCHEMA = 'ecommerce'
ORDER BY
    TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;
 */

@Slf4j
//@EnableBatchProcessing
@EnableCaching
@AllArgsConstructor
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}