package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.api.domain.DiscountDTO;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.repository.DiscountRepository;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    
    @Autowired
    private DiscountRepository discountRepository;
    
    public DiscountEntity getDiscountById(Long discountId) {
        return discountRepository.findById(discountId).orElseThrow(() -> new ResourceNotFoundException("해당 discount id에 해당하는 discount가 없습니다."));
    }
    
    public List<com.cho.ecommerce.api.domain.DiscountDTO> getAllDiscounts() {
        List<DiscountEntity> discountEntities = discountRepository.findAll();
        
        List<DiscountDTO> discountDTOS = new ArrayList<>();
        
        for(DiscountEntity discountEntity : discountEntities) {
            com.cho.ecommerce.api.domain.DiscountDTO discountDTO = new DiscountDTO();
            discountDTO.setDiscountId(discountEntity.getDiscountId());
            discountDTO.setDiscountType(discountEntity.getDiscountType().toString());
            discountDTO.setDiscountValue(discountEntity.getDiscountValue());
            discountDTO.setStartDate(discountEntity.getStartDate());
            discountDTO.setEndDate(discountEntity.getEndDate());
            
            discountDTOS.add(discountDTO);
        }
        
        return discountDTOS;
    }
}
