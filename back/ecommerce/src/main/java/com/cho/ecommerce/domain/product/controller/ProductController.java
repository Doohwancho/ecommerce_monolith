package com.cho.ecommerce.domain.product.controller;

import com.cho.ecommerce.api.ProductApi;
import com.cho.ecommerce.api.domain.PaginatedProductResponse;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductListResponseDTO;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.service.ProductService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductApi {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OptionRepository optionRepository;
    

    
    @Override
    public ResponseEntity<PaginatedProductResponse> getProductsWithPagiation(
        Integer page,
        Integer size
    ) {
        PaginatedProductResponse productsWithPagination = productService.getProductsWithPagination(
            page, size);
        
        return ResponseEntity.ok(productsWithPagination);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.ProductDetailResponseDTO>> getProductDetailDTOsById(Long id) {
        List<com.cho.ecommerce.api.domain.ProductDetailResponseDTO> productList = productService.findProductDetailDTOsById(id);
        
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
    
    
    @Override
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody com.cho.ecommerce.api.domain.ProductCreateRequestDTO product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    
    @Override
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
        @Valid @RequestBody ProductDTO product) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        product.setProductId(id); // Ensure the ID is set on the product to update
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    
    @Override
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @Override
    public ResponseEntity<ProductListResponseDTO> getProductsByCategory(Long categoryId) {
        ProductListResponseDTO allProductsByCategory = productService.findAllProductsByCategory(
            categoryId);
        
        return ResponseEntity.ok(allProductsByCategory);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO>> getAllCategoriesSortByDepth() {
        List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO> allCategoriesSortByDepth = categoryRepository.findAllCategoriesSortByDepth(); //TODO - 굳이 service layer안거치고 바로 controller가 repository에서 건져 올리는게 맞는걸까?
        return ResponseEntity.ok(allCategoriesSortByDepth);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO>> getOptionsByCategory(Long categoryId) {
        List<com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO> optionsAndOptionVariationsByCategoryId = optionRepository.findOptionsAndOptionVariationsByCategoryId(categoryId);
        return ResponseEntity.ok(optionsAndOptionVariationsByCategoryId);
    }
}
