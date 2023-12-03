package com.cho.ecommerce.util_test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import com.cho.ecommerce.global.util.DatabaseCleanup;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DataJpaTest
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("local")
@Tag("util") //to run, type "mvn test -Dgroups=smoke"
@Transactional
class DatabaseCleanupTest {
    
    @Autowired
    private DatabaseCleanup databaseCleanup;
    
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;
    
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private FakeDataGenerator dataGenerator;
    
    @Test
    void testDatabaseCleanup() {
        // Given
        long previousDBSize = categoryRepository.count();
        
        final Integer numberOfFakeCategories = 1;
        final Integer numberOfFakeOptions = 1;
        final Integer numberOfFakeOptionsVariations = 1;
    
        //When
        //insert data into database
        dataGenerator.generateFakeCategoryAndOptions(numberOfFakeCategories, numberOfFakeOptions,
            numberOfFakeOptionsVariations);
        
        // Then
        assertEquals(categoryRepository.count(), previousDBSize + 1);
        
        // When
        databaseCleanup.afterPropertiesSet();
        databaseCleanup.execute();
        
        // Then
        assertEquals(0, categoryRepository.count());
    }
}
