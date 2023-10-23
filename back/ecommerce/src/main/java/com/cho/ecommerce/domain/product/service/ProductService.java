package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;
    
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
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
}
