package com.cho.ecommerce.domain.product.domain;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

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
    
    // Static inner Builder class
    public static class Builder {
        private Long discountId;
        private DiscountType discountType;
        private Double discountValue;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        
        public Builder discountId(Long discountId) {
            this.discountId = discountId;
            return this;
        }
        
        public Builder discountType(DiscountType discountType) {
            this.discountType = discountType;
            return this;
        }
        
        public Builder discountValue(Double discountValue) {
            this.discountValue = discountValue;
            return this;
        }
        
        public Builder startDate(OffsetDateTime startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder endDate(OffsetDateTime endDate) {
            this.endDate = endDate;
            return this;
        }
        
        public Discount build() {
            Assert.notNull(discountType, "discount type이 null이 될 수 없습니다.");
            Assert.notNull(discountValue, "discount value가 null이 될 수 없습니다.");
            Assert.isTrue(discountValue >= 0, "discount률은 음수가 될 수 없습니다.");
            
            Discount discount = new Discount();
            discount.setDiscountId(discountId);
            discount.setDiscountType(discountType);
            discount.setDiscountValue(discountValue);
            discount.setStartDate(startDate);
            discount.setEndDate(endDate);
            return discount;
        }
    }
}
