package com.cho.ecommerce.domain.product.controller;

import com.cho.ecommerce.api.ProductApi;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController implements ProductApi {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> productDTOs = productService.getAllProducts()
            .stream()
            .map(productMapper::productEntityToProductDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }
    
    @Override
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(productMapper::productEntityToProductDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    
    @Override
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO product) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        product.setPRODUCTID(id); // Ensure the ID is set on the product to update
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    
    @Override
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
