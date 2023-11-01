package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.CategoryEntity;

public interface CategoryRepositoryCustom {
    CategoryEntity findByCategoryId(Long categoryId);
}
