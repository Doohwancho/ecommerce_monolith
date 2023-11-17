package com.cho.ecommerce.util_test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
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
    private OrderRepository orderEntityRepository;
    
    
    @LocalServerPort
    private int port;
    
    @Test
    void testDatabaseCleanup() {
        // Given
        long previousDBSize = orderEntityRepository.count();
        
        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PENDING");
        
        // When
        entityManager.persist(order);
        entityManager.flush();
        
        // Then
        assertEquals(orderEntityRepository.count(), previousDBSize + 1);
        
        // When
        databaseCleanup.afterPropertiesSet();
        databaseCleanup.execute();
        
        // Then
        assertEquals(0, orderEntityRepository.count());
    }
}
