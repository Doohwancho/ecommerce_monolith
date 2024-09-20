package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.repository.DiscountRepository;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    
    @Autowired
    private DiscountRepository discountRepository;
    
    public DiscountEntity getDiscountById(Long discountId) {
        return discountRepository.findById(discountId).orElseThrow(() -> new ResourceNotFoundException("해당 discount id에 해당하는 discount가 없습니다."));
    }
}
