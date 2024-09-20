package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.repository.ProductItemRepository;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductItemService {

    @Autowired
    private ProductItemRepository productItemRepository;

    public ProductItemEntity getById(Long id){
        return productItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product Item not found"));
    }
}
