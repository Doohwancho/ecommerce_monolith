package com.cho.ecommerce.domain.product.domain.converter;

import com.cho.ecommerce.domain.product.domain.DiscountType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DiscountTypeConverter implements AttributeConverter<DiscountType, String> {
    
    @Override
    public String convertToDatabaseColumn(DiscountType discountType) {
        return discountType != null ? discountType.getDescription() : null;
    }
    
    @Override
    public DiscountType convertToEntityAttribute(String value) {
        return value != null ? DiscountType.fromDescription(value) : null;
    }
}