package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.domain.Product.OptionDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "DENORMALIZED_PRODUCT", indexes = {
    @Index(name = "idx_category_id", columnList = "CATEGORY_ID"),
    @Index(name = "idx_rating_count", columnList = "RATING_COUNT"),
    @Index(name = "idx_rating", columnList = "RATING")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenormalizedProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;
    
    @NotBlank(message = "Name is required")
    @Column(name = "NAME")
    private String name;
    
    @NotBlank(message = "Description is required")
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be no more than 5")
    @Column(name = "RATING")
    private Double rating;
    
    @Min(0)
    @Column(name = "RATING_COUNT")
    private Integer ratingCount;
    
    @NotNull(message = "Category ID is required")
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    
    @NotBlank(message = "Category name is required")
    @Column(name = "CATEGORY_NAME")
    private String categoryName;
    
    @Min(0)
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    
    /* 구조
        {
          "options": [
            {
              "name": "Color",
              "values": ["Red", "Blue", "Green"]
            },
            {
              "name": "Size",
              "values": ["S", "M", "L", "XL"]
            }
          ]
        }
     */
    @Column(name = "OPTIONS", columnDefinition = "JSON")
    private String options;
    
    /* json 구조
    {
      "discounts": [
            {
              "discountId": 1,
              "discountType": "PERCENTAGE",
              "discountValue": 10.0,
              "startDate": "2023-09-01T00:00:00Z",
              "endDate": "2023-09-30T23:59:59Z"
            },
            {
              "discountId": 2,
              "discountType": "FLAT_RATE",
              "discountValue": 5.0,
              "startDate": "2023-10-01T00:00:00Z",
              "endDate": "2023-10-31T23:59:59Z"
            }
        ]
     }
     */
    @Column(name = "DISCOUNTS", columnDefinition = "JSON")
    private String discounts;
    
    // Additional fields for efficient querying
    @Column(name = "HAS_DISCOUNT")
    private Boolean hasDiscount;
    
    @Min(0)
    @Column(name = "BASE_PRICE")
    private Double basePrice;
    
    @Column(name = "LOWEST_PRICE")
    private Double lowestPrice;
    
    @Column(name = "HIGHEST_PRICE")
    private Double highestPrice;
    
    @Column(name = "LATEST_DISCOUNT_START")
    private OffsetDateTime latestDiscountStart;
    
    @Column(name = "LATEST_DISCOUNT_END")
    private OffsetDateTime latestDiscountEnd;
    
    
    // Methods for business logic
    @JsonIgnore
    public boolean isDiscountActive() {
        OffsetDateTime now = OffsetDateTime.now();
        return hasDiscount && now.isAfter(latestDiscountStart) && now.isBefore(latestDiscountEnd);
    }
    
    
    // Override toString() for logging
    @Override
    public String toString() {
        return "DenormalizedProduct{" +
            "productId=" + productId +
            ", name='" + name + '\'' +
            ", categoryName='" + categoryName + '\'' +
            ", basePrice=" + basePrice +
            ", totalQuantity=" + totalQuantity +
            ", hasDiscount=" + hasDiscount +
            ", lowestPrice=" + lowestPrice +
            ", highestPrice=" + highestPrice +
            '}';
    }
    
    /*****************************************************
     * Json fields methods
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /*****************************************************
     * json option method
     */
    public List<OptionDTO> getOptionsAsList() throws IOException {
        return objectMapper.readValue(this.options, new TypeReference<List<Product.OptionDTO>>(){});
    }
    
    public void setOptionsFromList(List<Product.OptionDTO> optionsList) throws IOException {
        this.options = objectMapper.writeValueAsString(optionsList);
    }
    
    
    /*****************************************************
     * json option method
     */
    public List<Product.DiscountDTO> getDiscountsAsList() throws IOException {
        if (this.discounts == null || this.discounts.isEmpty()) {
            return new ArrayList<>(); //TODO - null로 처리할 것인가? 아니면 empty array로 처리할 것인가? null을 시스템 에러나는 경우가 있어서 일단 empty array로 처리한다.
        }
        return objectMapper.readValue(this.discounts, new TypeReference<List<Product.DiscountDTO>>(){});
    }
    
    public void setDiscountsFromList(List<Product.DiscountDTO> discountsList) throws IOException {
        this.discounts = objectMapper.writeValueAsString(discountsList);
    }
    
    public boolean hasActiveDiscount() {
        try {
            List<Product.DiscountDTO> discountsList = getDiscountsAsList();
            OffsetDateTime now = OffsetDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            
            return discountsList.stream()
                .anyMatch(discount -> {
                    OffsetDateTime startDate = OffsetDateTime.parse(discount.getStartDate(), formatter);
                    OffsetDateTime endDate = OffsetDateTime.parse(discount.getEndDate(), formatter);
                    return now.isAfter(startDate) && now.isBefore(endDate);
                });
        } catch (IOException e) {
            // Log the exception
            return false;
        }
    }
    
    // You should also update the getEffectivePrice() method similarly:
    public Double getDiscountedPrice() {
        try {
            List<Product.DiscountDTO> discountsList = getDiscountsAsList();
            OffsetDateTime now = OffsetDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            double effectivePrice = this.basePrice;
            
            for (Product.DiscountDTO discount : discountsList) {
                OffsetDateTime startDate = OffsetDateTime.parse(discount.getStartDate(), formatter);
                OffsetDateTime endDate = OffsetDateTime.parse(discount.getEndDate(), formatter);
                if (now.isAfter(startDate) && now.isBefore(endDate)) {
                    if (discount.getType().equals("PERCENTAGE")) {
                        effectivePrice *= (1 - discount.getValue() / 100);
                    } else if (discount.getType().equals("FLAT_RATE")) {
                        effectivePrice -= discount.getValue();
                    }
                }
            }
            
            return Math.max(effectivePrice, 0);
        } catch (IOException e) {
            // Log the exception
            log.error("error on getEffectivePrice()", e);
            return this.basePrice;
        }
    }
}
