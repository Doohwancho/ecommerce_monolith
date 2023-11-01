package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import java.util.List;

public interface OptionRepositoryCustom {
    List<OptionEntity> findByCategory_CategoryId(Long categoryId);
}

