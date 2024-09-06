package com.cho.ecommerce.unit_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.service.ProductService;
import com.cho.ecommerce.global.config.bulk_insert.fakedata.step1_jpa_saveAll.JpaFakeDataGenerator;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("local")
@Tag("unit") //to run, type "mvn test -Dgroups=integration"
class ProductUnitTest {
    
    @Autowired
    private ProductService productService;
    @Autowired
    private JpaFakeDataGenerator dataGenerator;
    
    @Test
    void GetProductDetailDTOsByIdUnitTest() {
        // Given
        Long productId = 1L;
        
        final Integer numberOfFakeCategories = 10;
        final Integer numberOfFakeOptions = 3;
        final Integer numberOfFakeOptionsVariations = 3;
        final Integer numberOfFakeProducts = 10;
        final Integer numberOfFakeProductItems = 3;
        
        dataGenerator.generateFakeCategoryAndOptions(numberOfFakeCategories, numberOfFakeOptions,
            numberOfFakeOptionsVariations);
        dataGenerator.generateFakeProducts(numberOfFakeProducts, numberOfFakeCategories,
            numberOfFakeProductItems);
        
        // When
        List<Product> products = productService.getProductDetailDTOsById(productId);
        
        // Then
        for (Product product : products) {
            assertNotNull(products);
            assertEquals(products.size(), numberOfFakeProductItems);
            assertNotNull(product.getProductId());
            assertNotNull(product.getName());
            assertNotNull(product.getDescription());
            assertNotNull(product.getRating());
            assertNotNull(product.getRatingCount());
            assertNotNull(product.getQuantity());
            assertNotNull(product.getPrice());
            assertNotNull(product.getDiscounts());
            assertNotNull(product.getCategoryId());
            assertNotNull(product.getCategoryCode());
            assertNotNull(product.getCategoryName());
            assertNotNull(product.getOptionName());
            assertNotNull(product.getOptionVariationName());
        }
    }
}
