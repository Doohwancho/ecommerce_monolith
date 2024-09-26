package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.dto.ProductCreateRequestDTO;
import com.cho.ecommerce.domain.product.dto.ProductResponseDTO;
import com.cho.ecommerce.domain.product.entity.DenormalizedProductEntity;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public List<Product> getAllProducts() {
        List<DenormalizedProductEntity> allProducts = productRepository.findAll();
        return allProducts.stream().map(ProductMapper::convertToProduct).collect(Collectors.toList());
    }
    public Product getProduct(Long productId) throws IOException {
        DenormalizedProductEntity entity = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException(productId + " does not exist in database"));
        
        Product product = new Product();
        BeanUtils.copyProperties(entity, product);
        product.setOptions(entity.getOptionsAsList());
        product.setDiscounts(entity.getDiscountsAsList());
        
        return product;
    }
    
    public Product getProductByName(String productName) {
        DenormalizedProductEntity entity = productRepository.findProductsByName(productName);
        if(entity == null) {
            throw new RuntimeException("product's named " + productName + " does not exists!");
        }
        return ProductMapper.convertToProduct(entity);
    }
    
    public ProductResponseDTO getProductResponse(Long productId) {
        DenormalizedProductEntity entity = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException(productId + " does not exist in database"));
        
        return ProductMapper.convertToProductResponseDTO(entity);
    }
    
    public Page<ProductResponseDTO> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        Page<DenormalizedProductEntity> entityPage = productRepository.findByCategoryId(categoryId, pageable);
        return entityPage.map(ProductMapper::convertToProductResponseDTO);
    }
    public List<ProductResponseDTO> getTopRatedProducts(int limit) {
        List<DenormalizedProductEntity> topRatedEntities = productRepository.findTopTenRatedProducts(
            PageRequest.of(0, limit));
        return topRatedEntities.stream()
            .map(ProductMapper::convertToProductResponseDTO)
            .collect(Collectors.toList());
    }
    
    public ProductResponseDTO createProductResponse(ProductCreateRequestDTO productRequestDto) throws IOException {
        DenormalizedProductEntity entity = new DenormalizedProductEntity();
        BeanUtils.copyProperties(productRequestDto, entity);
        
        entity.setOptionsFromList(productRequestDto.getOptions());
        entity.setDiscountsFromList(productRequestDto.getDiscounts());
        
        // Calculate additional fields
        entity.setRating(0.0);
        entity.setRatingCount(0);
        entity.setHasDiscount(!productRequestDto.getDiscounts().isEmpty());
        entity.setLowestPrice(entity.getDiscountedPrice());
        entity.setHighestPrice(entity.getBasePrice());
        
        if (entity.getHasDiscount()) {
            OffsetDateTime latestStart = null;
            OffsetDateTime latestEnd = null;
            for (Product.DiscountDTO discount : productRequestDto.getDiscounts()) {
                OffsetDateTime start = discount.getStartDate();
                OffsetDateTime end = discount.getEndDate();
                if (latestStart == null || start.isAfter(latestStart)) {
                    latestStart = start;
                }
                if (latestEnd == null || end.isAfter(latestEnd)) {
                    latestEnd = end;
                }
            }
            entity.setLatestDiscountStart(latestStart);
            entity.setLatestDiscountEnd(latestEnd);
        }
        
        DenormalizedProductEntity savedEntity = productRepository.save(entity);
        
        return ProductMapper.convertToProductResponseDTO(savedEntity);
    }
    
    @Transactional
    public void decreaseStock(String productName, int quantity) {
        DenormalizedProductEntity product = productRepository.findProductsByName(productName);
        if (product == null) {
            throw new RuntimeException("Product " + productName + " not found");
        }
        
        int newQuantity = product.getTotalQuantity() - quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock for product " + productName);
        }
        
        product.setTotalQuantity(newQuantity);
        productRepository.save(product);
    }
}