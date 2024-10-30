package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.domain.product.domain.Discount;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-30T15:59:35+0900",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.40.0.v20240919-1711, environment: Java 17.0.12 (Eclipse Adoptium)"
)
@Component
public class DiscountMapperImpl implements DiscountMapper {

    @Override
    public Discount discountEntityToDiscount(DiscountEntity discountEntity) {
        if ( discountEntity == null ) {
            return null;
        }

        Discount discount = new Discount();

        discount.setDiscountId( discountEntity.getDiscountId() );
        discount.setDiscountType( discountEntity.getDiscountType() );
        discount.setDiscountValue( discountEntity.getDiscountValue() );
        discount.setEndDate( discountEntity.getEndDate() );
        discount.setStartDate( discountEntity.getStartDate() );

        return discount;
    }

    @Override
    public List<Discount> discountEntitiesToDiscounts(List<DiscountEntity> discountEntityList) {
        if ( discountEntityList == null ) {
            return null;
        }

        List<Discount> list = new ArrayList<Discount>( discountEntityList.size() );
        for ( DiscountEntity discountEntity : discountEntityList ) {
            list.add( discountEntityToDiscount( discountEntity ) );
        }

        return list;
    }
}
