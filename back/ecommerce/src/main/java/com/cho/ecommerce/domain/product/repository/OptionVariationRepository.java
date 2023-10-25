package com.cho.ecommerce.domain.product.repository;


import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionVariationRepository extends JpaRepository<OptionVariationEntity, Long> {

}
