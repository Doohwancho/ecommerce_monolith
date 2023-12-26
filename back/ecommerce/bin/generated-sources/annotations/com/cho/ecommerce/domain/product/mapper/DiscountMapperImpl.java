package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.domain.product.domain.Discount;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-25T17:47:09+0900",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.9 (Eclipse Adoptium)"
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
