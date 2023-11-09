package com.cho.ecommerce.domain.product.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Discount {
    
    private Long discountId;
    private DiscountType discountType;
    private Double discountValue;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    
    public Double applyDiscount(Double originalPrice) {
        if(OffsetDateTime.now().isAfter(endDate)) return originalPrice;
        
        switch (discountType) {
            case PERCENTAGE:
                return originalPrice * (1 - discountValue / 100);
            case FLAT_RATE:
                double discountedPrice = originalPrice - discountValue;
                if (discountedPrice < 0) {
                    discountedPrice = 0.0;
                }
                return discountedPrice;
            default:
                return originalPrice;
        }
    }
}
