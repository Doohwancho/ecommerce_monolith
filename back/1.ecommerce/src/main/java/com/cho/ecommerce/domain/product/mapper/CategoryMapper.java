package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.api.domain.CategoryOptionsOptionVariationsResponseDTO;
import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    
    public CategoryOptionsOptionVariationsResponseDTO mapToCategoryOptionsResponse(
        List<Tuple> results) {
        if (results.isEmpty()) {
            return null;
        }
        
        Tuple firstResult = results.get(0);
        CategoryOptionsOptionVariationsResponseDTO response = new CategoryOptionsOptionVariationsResponseDTO()
            .categoryId(firstResult.get(0, Long.class))
            .categoryName(firstResult.get(1, String.class));
        
        Map<Long, com.cho.ecommerce.api.domain.OptionDTO> optionMap = new LinkedHashMap<>();
        
        for (Tuple tuple : results) {
            Long optionId = tuple.get(2, Long.class);
            com.cho.ecommerce.api.domain.OptionDTO optionDTO = optionMap.computeIfAbsent(optionId,
                k -> new com.cho.ecommerce.api.domain.OptionDTO()
                    .optionId(optionId)
                    .optionValue(tuple.get(3, String.class))
                    .optionVariations(new ArrayList<>()));
            
            Long optionVariationId = tuple.get(4, Long.class);
            if (optionVariationId != null) {
                com.cho.ecommerce.api.domain.OptionVariationDTO variationDTO = new com.cho.ecommerce.api.domain.OptionVariationDTO()
                    .optionVariationId(optionVariationId)
                    .optionVariationValue(tuple.get(5, String.class));
                optionDTO.getOptionVariations().add(variationDTO);
            }
        }
        
        response.setOptions(new ArrayList<>(optionMap.values()));
        return response;
    }
}
