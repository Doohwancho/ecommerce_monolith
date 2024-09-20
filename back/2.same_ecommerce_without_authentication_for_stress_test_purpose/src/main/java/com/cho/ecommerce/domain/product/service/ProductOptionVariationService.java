package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.ProductOptionVariationRepository;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductOptionVariationService {

    @Autowired
    private ProductOptionVariationRepository productOptionVariationRepository;
    
    public ProductOptionVariationEntity getProductOptionVariationById(Long id) {
        return productOptionVariationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("해당 productOptionVariation id에 해당하는 엔티티가 존재하지 않습니다."));
    }
}
