package com.cho.ecommerce.domain.product.repository;

public interface OptionVariationRepositoryCustom {
    
    public com.cho.ecommerce.api.domain.CategoryOptionsOptionVariationsResponseDTO findCategoryOptionsAndVariations(
        Long categoryId);
}
