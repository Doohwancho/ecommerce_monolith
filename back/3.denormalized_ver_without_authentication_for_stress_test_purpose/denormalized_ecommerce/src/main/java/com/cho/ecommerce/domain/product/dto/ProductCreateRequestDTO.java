package com.cho.ecommerce.domain.product.dto;

import com.cho.ecommerce.domain.product.domain.Product.DiscountDTO;
import com.cho.ecommerce.domain.product.domain.Product.OptionDTO;
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
