package com.cho.ecommerce.domain.product.domain;

import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.global.error.exception.business.RiggedDiscountRequested;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
public class Discount implements Serializable { //redis에 json으로 serialize 해서 저장해서 필요
    
    private static final long serialVersionUID = 1L; //redis에 json으로 serialize 해서 저장해서 필요
    
    private Long discountId;
    private DiscountType discountType;
    private Double discountValue;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    
    public Double applyDiscount(Double originalPrice) {
        if (OffsetDateTime.now().isAfter(endDate)) {
            return originalPrice;
        }
        
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
    
    public Boolean validateRequestedDiscountWithSavedDiscountEntity(DiscountEntity discountEntity) {
        if (!(discountId.equals(discountEntity.getDiscountId()) &&
            discountType.equals(discountEntity.getDiscountType()) &&
            discountValue.equals(discountEntity.getDiscountValue()) &&
            startDate.isEqual(discountEntity.getStartDate()) &&
            endDate.isEqual(discountEntity.getEndDate()))) {
            throw new RiggedDiscountRequested("요청한 Discount의 값은 데이터베이스에 존재하지 않거나 조작되었습니다.");
        }
        return true;
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
