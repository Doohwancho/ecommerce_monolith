package com.ecommerce.monolith.domain.product.dto;

import com.ecommerce.monolith.domain.product.domain.Product.DiscountDTO;
import com.ecommerce.monolith.domain.product.domain.Product.OptionDTO;
import java.util.List;
import lombok.Data;

@Data
public class ProductCreateRequestDTO {
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Integer totalQuantity;
    private List<OptionDTO> options;
    private List<DiscountDTO> discounts;
    private Double basePrice;
}
