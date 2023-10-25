package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
