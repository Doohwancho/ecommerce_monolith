package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.domain.product.domain.Discount;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    
    DiscountMapper INSTANCE = Mappers.getMapper(DiscountMapper.class);
    
    Discount discountEntityToDiscount(DiscountEntity discountEntity);
    
    List<Discount> discountEntitiesToDiscounts(List<DiscountEntity> discountEntityList);
}

