package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionService {
    
    @Autowired
    private OptionRepository optionEntityRepository;
    
    @Autowired
    private OptionVariationRepository optionVariationRepository;
    
    public OptionEntity createOption(OptionEntity option) {
        return optionEntityRepository.save(option);
    }
    
    public OptionEntity getOptionById(Long optionId) {
        return optionEntityRepository.findById(optionId)
            .orElseThrow(() -> new RuntimeException("Option not found with ID: " + optionId));
    }
    
    public OptionEntity updateOption(Long optionId, OptionEntity updatedOption) {
        OptionEntity existingOption = getOptionById(optionId);
        existingOption.setValue(updatedOption.getValue());
        
        return optionEntityRepository.save(existingOption);
    }
    
    public void deleteOption(Long optionId) {
        OptionEntity option = getOptionById(optionId);
        optionEntityRepository.delete(option);
    }
    
    public List<OptionEntity> getAllOptions() {
        return optionEntityRepository.findAll();
    }
    
    public OptionVariationEntity createOptionVariation(OptionVariationEntity optionVariation) {
        return optionVariationRepository.save(optionVariation);
    }
    
    public OptionVariationEntity getOptionVariationById(Long optionVariationId) {
        return optionVariationRepository.findById(optionVariationId)
            .orElseThrow(() -> new RuntimeException("Option Variation not found with ID: " + optionVariationId));
    }
    
    public OptionVariationEntity updateOptionVariation(Long optionVariationId, OptionVariationEntity updatedOptionVariation) {
        OptionVariationEntity existingOptionVariation = getOptionVariationById(optionVariationId);
        existingOptionVariation.setValue(updatedOptionVariation.getValue());
        return optionVariationRepository.save(existingOptionVariation);
    }
    
    public void deleteOptionVariation(Long optionVariationId) {
        OptionVariationEntity optionVariation = getOptionVariationById(optionVariationId);
        optionVariationRepository.delete(optionVariation);
    }
    
    public List<OptionVariationEntity> getAllOptionVariationsForOption(Long optionId) {
        OptionEntity option = getOptionById(optionId);
        return new ArrayList<>(option.getOptionVariations());
    }
}

