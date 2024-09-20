package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {

}
