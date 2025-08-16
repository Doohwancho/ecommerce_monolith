package com.ecommerce.monolith.domain.product.dto;

import com.ecommerce.monolith.domain.product.domain.Product.OptionDTO;
import java.util.List;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long productId;
    private String productName;
    private String description;
    private Double rating;
    private Integer ratingCount;
    private Long categoryId;
    private String categoryName;
    private List<OptionDTO> options;
    private Double lowestPrice;
    private Double basePrice;
}
