package com.ecommerce.monolith.domain.product.controller;

import com.ecommerce.monolith.domain.product.domain.Product;
import com.ecommerce.monolith.domain.product.dto.PaginatedProductResponse;
import com.ecommerce.monolith.domain.product.dto.ProductCreateRequestDTO;
import com.ecommerce.monolith.domain.product.dto.ProductResponseDTO;
import com.ecommerce.monolith.domain.product.service.ProductService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId) {
        ProductResponseDTO product = productService.getProductResponse(productId);
        return ResponseEntity.ok(product);
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProduct() {
        List<Product> product = productService.getAllProducts();
        return ResponseEntity.ok(product);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PaginatedProductResponse> getProductsByCategoryId(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
//        @RequestParam(defaultValue = "name") String sortBy,
//        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
    
//        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Pageable pageable = PageRequest.of(page, size);
    
        Page<ProductResponseDTO> productPage = productService.getProductsByCategoryId(categoryId, pageable);
        PaginatedProductResponse response = new PaginatedProductResponse(productPage);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/highestRatings")
    public ResponseEntity<List<ProductResponseDTO>> getTopRatedProducts(@RequestParam(defaultValue = "10") int limit) {
        List<ProductResponseDTO> topRatedProducts = productService.getTopRatedProducts(Math.min(limit, 100));
        return ResponseEntity.ok(topRatedProducts);
    }
    
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductCreateRequestDTO productCreateRequestDTO)
        throws IOException {
        ProductResponseDTO createdProduct = productService.createProductResponse(productCreateRequestDTO);
        return ResponseEntity.ok(createdProduct);
    }
}
