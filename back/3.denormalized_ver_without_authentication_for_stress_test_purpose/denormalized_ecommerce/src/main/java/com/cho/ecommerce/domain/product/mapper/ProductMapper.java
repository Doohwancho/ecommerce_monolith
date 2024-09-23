package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.domain.product.dto.ProductResponseDTO;
import com.cho.ecommerce.domain.product.entity.DenormalizedProductEntity;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductMapper {
    public static ProductResponseDTO convertToProductResponseDTO(DenormalizedProductEntity entity) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setRating(entity.getRating());
        dto.setRatingCount(entity.getRatingCount());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCategoryName(entity.getCategoryName());
        try {
            dto.setOptions(entity.getOptionsAsList());
        } catch (IOException e) {
            log.error("Error parsing options: {}", entity.getOptions(), e);
            dto.setOptions(new ArrayList<>()); // Set empty list as fallback
        }
        try {
            dto.setLowestPrice(entity.getDiscountedPrice());
        } catch (Exception e) {
            log.error("Error calculating discounted price", e);
            dto.setLowestPrice(entity.getBasePrice()); // Use base price as fallback
        }
        dto.setBasePrice(entity.getBasePrice());
        return dto;
    }
}
