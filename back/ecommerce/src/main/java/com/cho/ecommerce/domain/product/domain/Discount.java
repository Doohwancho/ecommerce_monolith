package com.cho.ecommerce.domain.product.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Discount {
    
    private Long discountId;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Date startDate;
    private Date endDate;
    
    public Double applyDiscount(Double originalPrice) {
        switch (discountType) {
            case PERCENTAGE:
                return originalPrice * (1 - discountValue.doubleValue() / 100);
            case FLAT_RATE:
                return originalPrice - discountValue.doubleValue();
            default:
                return originalPrice;
        }
    }
}
