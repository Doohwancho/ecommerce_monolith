package com.cho.ecommerce.domain.product.repository;


import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OptionVariationRepository extends JpaRepository<OptionVariationEntity, Long>,
    OptionVariationRepositoryCustom {
    
    List<OptionVariationEntity> findByOption_OptionId(Long optionId);
    
}
