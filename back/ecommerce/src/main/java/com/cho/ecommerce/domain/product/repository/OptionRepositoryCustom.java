package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import java.util.List;

public interface OptionRepositoryCustom {
    List<OptionEntity> findOptionsByCategory_CategoryId(Long categoryId);
    
    List<com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO> findOptionsAndOptionVariationsByCategoryId(Long categoryId);
}

