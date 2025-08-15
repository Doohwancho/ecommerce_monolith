package com.cho.ecommerce.domain.product.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
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
    
    @JsonIgnoreProperties
    @Data
    public static class OptionDTO {
        public OptionDTO() {}
        @JsonProperty("name")
        private String name;
        @JsonProperty("values")
        private List<String> values;
    }
    
    @JsonIgnoreProperties
    @Data
    public static class DiscountDTO {
        public DiscountDTO() {}
        @JsonProperty("type")
        private String type;
        @JsonProperty("value")
        private Double value;
        @JsonProperty("startDate")
        private OffsetDateTime startDate;
        @JsonProperty("endDate")
        private OffsetDateTime endDate;
    }
}