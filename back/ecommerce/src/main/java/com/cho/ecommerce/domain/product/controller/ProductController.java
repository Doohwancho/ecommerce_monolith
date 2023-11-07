package com.cho.ecommerce.domain.product.controller;

import com.cho.ecommerce.api.ProductApi;
import com.cho.ecommerce.api.domain.ProductCreateDTO;
import com.cho.ecommerce.api.domain.ProductDetail;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductListResponse;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductApi {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductMapper productMapper;
    
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductEntity> allProducts = productService.getAllProducts();
        List<ProductDTO> productDTOS = productMapper.INSTANCE.productEntitiesToProductDTOs(
            allProducts);
    
        return ResponseEntity.ok(productDTOS);
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<List<ProductDetail>> getProductDetailsById(Long id) {
        List<ProductDetail> productList = productService.findProductDetailsById(id);
        
        return new ResponseEntity<>(productList, HttpStatus.CREATED);
    }

    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductCreateDTO product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO product) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        product.setProductId(id); // Ensure the ID is set on the product to update
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<ProductListResponse> getProductsByCategory(Long categoryId) {
        ProductListResponse allProductsByCategory = productService.findAllProductsByCategory(
            categoryId);
    
        return ResponseEntity.ok(allProductsByCategory);
    }
}
