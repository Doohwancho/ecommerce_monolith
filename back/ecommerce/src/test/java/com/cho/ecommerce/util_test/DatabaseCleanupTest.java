package com.cho.ecommerce.util_test;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import com.cho.ecommerce.global.util.DatabaseCleanup;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DataJpaTest
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
@Tag("util") //to run, type "mvn test -Dgroups=smoke"
@Transactional
public class DatabaseCleanupTest {
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
        assertEquals(orderEntityRepository.count(), 0);
    }
}
