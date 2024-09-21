package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.domain.product.dto.ProductResponseDTO;
import com.cho.ecommerce.domain.product.entity.DenormalizedProductEntity;
import java.io.IOException;
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
            log.error("convertToProductResponseDTO() error", e);
        }
        dto.setLowestPrice(entity.getDiscountedPrice());
        dto.setBasePrice(entity.getBasePrice());
        return dto;
    }
}
