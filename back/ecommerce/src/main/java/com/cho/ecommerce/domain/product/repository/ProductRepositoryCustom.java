package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.ProductEntity;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryCustom {
    List<ProductEntity> findAllProductsByCategory(Long categoryId);
    
    Optional<List<ProductEntity>> findProductDetailDTOsById(Long productId);
    
    List<ProductEntity> findTop10ByRating();
}
