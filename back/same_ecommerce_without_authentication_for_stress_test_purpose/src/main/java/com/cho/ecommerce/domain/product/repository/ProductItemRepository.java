package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItemEntity, Long> {

}
