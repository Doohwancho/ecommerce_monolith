package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.ProductEntity;
import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductEntity> findAllProductsByCategory(Long categoryId);
    
    List<ProductEntity> findProductDetailDTOsById(Long productId);
}
