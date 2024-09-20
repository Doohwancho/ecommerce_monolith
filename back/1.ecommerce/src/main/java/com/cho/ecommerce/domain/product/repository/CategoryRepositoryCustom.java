package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import java.util.List;

public interface CategoryRepositoryCustom {
    CategoryEntity findByCategoryId(Long categoryId);
    
    List<CategoryEntity> findCategoriesByParentName(String parentName);
    
    List<CategoryEntity> findCategoriesByDepth(Integer depth);
    
    List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO> findAllCategoriesSortByDepth();
}
