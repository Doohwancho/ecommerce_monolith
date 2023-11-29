package com.cho.ecommerce.unit_test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import com.cho.ecommerce.domain.order.entity.nativeQuery.OrderSalesStatisticsInterface;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.order.repository.OrderRepositoryCustomImpl;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest()
@ContextConfiguration(classes = {Application.class})
//@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("local")
@Tag("unit") //to run, type "mvn test -Dgroups=integration"
public class OrderUnitTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderRepositoryCustomImpl orderRepositoryCustom;
    
    @Test
    public void testGetOrderItemDetailsByUsername() {
        // Given: a username and corresponding data in the database
        
        // When: calling getOrderItemDetailsByUsername
        List<OrderItemDetails> results = orderRepositoryCustom.getOrderItemDetailsByUsername("testUser").get();
        
        // Then: verify the results
        assertNotNull(results);
        assertFalse(results.isEmpty());
    
        for (OrderItemDetails details : results) {
            assertNotNull(details.getOrderId());
            assertNotNull(details.getOrderDate());
            assertNotNull(details.getOrderStatus());
            assertNotNull(details.getMemberId());
            assertNotNull(details.getUsername());
            assertNotNull(details.getEmail());
            assertNotNull(details.getName());
            assertNotNull(details.getRole());
            assertNotNull(details.getEnabled());
            assertNotNull(details.getCreatedAt());
            assertNotNull(details.getUpdatedAt());
            assertNotNull(details.getProductId());
            assertNotNull(details.getProductName());
            assertNotNull(details.getDescription());
            assertNotNull(details.getRating());
            assertNotNull(details.getRatingCount());
            assertNotNull(details.getOptionValue());
            assertNotNull(details.getOptionVariationValue());
            assertNotNull(details.getQuantity());
            assertNotNull(details.getPrice());
            assertNotNull(details.getOrderItemId());
        }
    }
    
    @Test
    public void testFindMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths() {
        // Given
        
        // When
        List<OrderSalesStatisticsInterface> results = orderRepository.findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths();
        
        //Then
        results.forEach(result -> {
            assertNotNull(result.getCategoryId());
            assertNotNull(result.getCategoryName());
            assertNotNull(result.getNumberOfProductsPerCategory());
            assertNotNull(result.getAverageRating());
            assertNotNull(result.getTotalSalesPerCategory());
            assertNotNull(result.getProductId());
            assertNotNull(result.getTopSalesProductName());
            assertNotNull(result.getTopSalesOfProduct());
        });
    }
}
