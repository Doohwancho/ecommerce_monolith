package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.api.domain.ProductCreateDTO;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductListResponse;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepositoryCustomImpl;
import java.util.List;
import java.util.Optional;
import java.util.logging.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductRepositoryCustomImpl productRepositoryCustom;
    
    @Autowired
    private ProductMapper productMapper;
    
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public ProductDTO saveProduct(ProductCreateDTO product) {
        ProductEntity productEntity = productMapper.productCreateDTOToProductEntity(product);
        ProductEntity savedProduct = productRepository.save(productEntity);
        ProductDTO productDTO = productMapper.productEntityToProductDTO(savedProduct);
        return productDTO;
    }
    
    public ProductDTO saveProduct(ProductDTO product) {
        ProductEntity productEntity = productMapper.productDTOToProductEntity(product);
        ProductEntity savedProduct = productRepository.save(productEntity);
        ProductDTO productDTO = productMapper.productEntityToProductDTO(savedProduct);
        return productDTO;
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<ProductListResponse> findAllProductsByCategory(Long categoryId) {
        List<ProductEntity> allProductsByCategory = productRepositoryCustom.findAllProductsByCategory(
            categoryId);
        
        log.info("여기 왔다!");
        log.info(allProductsByCategory.toString());
        return productMapper.productEntitiesToProductListResponses(allProductsByCategory);
    }
}
