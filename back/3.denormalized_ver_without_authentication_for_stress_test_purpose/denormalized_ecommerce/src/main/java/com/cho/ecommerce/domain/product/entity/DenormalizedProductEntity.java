package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.domain.Product.OptionDTO;
import com.cho.ecommerce.global.config.parser.ObjectMapperUtil;
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
    @Index(name = "IDX_CATEGORY_ID", columnList = "CATEGORY_ID"),
    @Index(name = "IDX_RATING_COUNT", columnList = "RATING_COUNT"),
    @Index(name = "IDX_RATING", columnList = "RATING")
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
    private ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getObjectMapper();
    }
    
    /*****************************************************
     * json option method
     */
    public List<OptionDTO> getOptionsAsList() throws IOException {
        if (this.options == null || this.options.isEmpty()) {
            return new ArrayList<>();
        }
        //주의! 이 처리를 미리 해주지 않으면, objectMapper.readValue()에서 파싱 에러난다!
        //database에서 read 해왔을 때는 ""[{\"name\":"hello\", ...
        //여기서 맨 앞에 "와 중간에 섞인 \, backspace 때문에 objectMapper.readValue() 시 에러난다.
        //따라서 이 둘을 먼저 없애 준 후 파싱해야 한다.
        String jsonString = this.options;
        if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }
        // Unescape the inner quotes
        jsonString = jsonString.replace("\\\"", "\"");
//        log.info("Cleaned JSON string: " + jsonString);
        return getObjectMapper().readValue(jsonString, new TypeReference<List<Product.OptionDTO>>(){});
    }
    
    public void setOptionsFromList(List<Product.OptionDTO> optionsList) throws IOException {
        this.options = getObjectMapper().writeValueAsString(optionsList);
    }
    
    
    /*****************************************************
     * json option method
     */
    public List<Product.DiscountDTO> getDiscountsAsList() throws IOException {
        if (this.discounts == null || this.discounts.isEmpty()) {
            return new ArrayList<>();
        }
    
        //주의! 이 처리를 미리 해주지 않으면, objectMapper.readValue()에서 파싱 에러난다!
        //database에서 read 해왔을 때는 ""[{\"name\":"hello\", ...
        //여기서 맨 앞에 "와 중간에 섞인 \, backspace 때문에 objectMapper.readValue() 시 에러난다.
        //따라서 이 둘을 먼저 없애 준 후 파싱해야 한다.
        String jsonString = this.discounts;
        if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }
        // Unescape the inner quotes
        jsonString = jsonString.replace("\\\"", "\"");
        try {
            return getObjectMapper().readValue(jsonString, new TypeReference<List<Product.DiscountDTO>>(){});
        } catch (IOException e) {
            log.error("Error parsing discounts JSON: " + jsonString, e);
            throw e;
        }
    }
    
    public void setDiscountsFromList(List<Product.DiscountDTO> discountsList) throws IOException {
        this.discounts = getObjectMapper().writeValueAsString(discountsList);
    }
    
    public boolean hasActiveDiscount() {
        try {
            List<Product.DiscountDTO> discountsList = getDiscountsAsList();
            OffsetDateTime now = OffsetDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            
            return discountsList.stream()
                .anyMatch(discount -> {
                    return now.isAfter(discount.getStartDate()) && now.isBefore(discount.getEndDate());
                });
        } catch (IOException e) {
            // Log the exception
            return false;
        }
    }
    
    public Double getDiscountedPrice() {
        try {
            List<Product.DiscountDTO> discountsList = getDiscountsAsList();
            OffsetDateTime now = OffsetDateTime.now();
            double effectivePrice = this.basePrice;
            
            for (Product.DiscountDTO discount : discountsList) {
                OffsetDateTime startDate = discount.getStartDate();
                OffsetDateTime endDate = discount.getEndDate();
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
            log.error("Error parsing discounts JSON: {}", this.discounts, e);
            return this.basePrice;
        } catch (Exception e) {
            log.error("Unexpected error in getDiscountedPrice", e);
            return this.basePrice;
        }
    }
}
