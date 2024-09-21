package com.cho.ecommerce.domain.product.domain;

import java.util.List;
import lombok.Data;

@Data
public class Product {
    private Long productId;
    private String name;
    private String description;
    private Double rating;
    private Integer ratingCount;
    private Long categoryId;
    private String categoryName;
    private Integer totalQuantity;
    private List<OptionDTO> options;
    private List<DiscountDTO> discounts;
    private Double basePrice;
    private Double lowestPrice;
    private Double highestPrice;
    
    @Data
    public static class OptionDTO {
        private String name;
        private List<String> values;
    }
    
    @Data
    public static class DiscountDTO {
        private String type;
        private Double value;
        private String startDate;
        private String endDate;
    }
}