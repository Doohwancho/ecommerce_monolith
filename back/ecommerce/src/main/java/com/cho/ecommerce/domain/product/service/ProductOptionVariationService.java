package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.ProductOptionVariationRepository;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductOptionVariationService {
    @Autowired
    private ProductOptionVariationRepository productOptionVariationRepository;
    
    public ProductOptionVariationEntity saveProductOptionVariation(ProductOptionVariationEntity productOptionVariation) {
        return productOptionVariationRepository.save(productOptionVariation);
    }
    
    public ProductOptionVariationEntity getProductOptionVariationById(Long id) {
        return productOptionVariationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("productOptionVariation is not found"));
    }
    
    public List<ProductOptionVariationEntity> getAllProductOptionVariations() {
        return productOptionVariationRepository.findAll();
    }
    
//    public ProductOptionVariationEntity updateProductOptionVariation(Long id, ProductOptionVariationEntity updatedProductOptionVariation) {
//        return productOptionVariationRepository.findById(id)
//            .map(productOptionVariation -> {
//                return productOptionVariationRepository.save(productOptionVariation);
//            })
//            .orElseGet(() -> {
//                return null;
//            });
//    }
    
    public void deleteProductOptionVariation(Long id) {
        productOptionVariationRepository.deleteById(id);
    }
}
