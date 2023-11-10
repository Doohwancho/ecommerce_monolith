package com.cho.ecommerce.property_based_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.product.domain.Discount;
import com.cho.ecommerce.domain.product.domain.DiscountType;
import com.cho.ecommerce.domain.product.domain.Product;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.time.api.DateTimes;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@PropertyDefaults(tries = 100)
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
@Tag("pbt") //to run, type "mvn test -Dgroups=pbt"
public class ProductPriceDiscountTest {
    
    @Provide
    Arbitrary<OffsetDateTime> offsetDateTimes() {
        OffsetDateTime now = OffsetDateTime.now();
        
        return DateTimes.offsetDateTimes()
            .atTheEarliest(now.minusYears(1).toLocalDateTime())
            .atTheLatest(now.plusYears(1).toLocalDateTime()); //오늘 기준, 과거 -1년 ~ 미래 1년 사이 랜덤한 시간 선정함.
    }
    
    @Property
    void getPERCENTAGEDiscountedPriceOfProduct(
        @ForAll @DoubleRange (min = 0.0) Double originalPrice,
        @ForAll @DoubleRange(min = 0.01, max = 100.00) double discountValue,
        @ForAll("offsetDateTimes") OffsetDateTime endDate
    ) {
        //given
        List<Discount> discountList = new ArrayList<>();
        
        for(int i = 0; i < 3; i++) {
            Discount discount = new Discount.Builder()
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(discountValue)
                .endDate(endDate)
                .build();
            discountList.add(discount);
        }
        
        Product product = new Product.Builder()
            .price(originalPrice)
            .discounts(discountList)
            //rest of properties are not necessary for this test, but fill it to avoid exception handling
            .quantity(0)
            .name("")
            .categoryId(0L)
            .categoryName("")
            .categoryCode("")
            .optionName("")
            .build();
        
        //when
        Double discountedPrice = product.getDiscountedPrice();
        
        //then
        Double targetDiscountedPrice = discountList.stream()
            .reduce(originalPrice,
                (price, discount) -> discount.applyDiscount(price),
                (price1, price2) -> price2
            );
        
        assertEquals(discountedPrice, targetDiscountedPrice);
    }
    
    
    @Property
    void getFLATRATEDiscountedPriceOfProduct(
        @ForAll @DoubleRange(min = 0.0) Double originalPrice,
        @ForAll @DoubleRange(min = 1000.0, max = 1_000_000.0) double discountValue,
        @ForAll("offsetDateTimes") OffsetDateTime endDate
    ) {
        //given
        List<Discount> discountList = new ArrayList<>();
        
        for(int i = 0; i < 3; i++) {
            Discount discount = new Discount.Builder()
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(discountValue)
                .endDate(endDate)
                .build();
            discountList.add(discount);
        }
        
        Product product = new Product.Builder()
            .price(originalPrice)
            .discounts(discountList)
            //rest of properties are not necessary for this test, but fill it to avoid exception handling
            .quantity(0)
            .name("")
            .categoryId(0L)
            .categoryName("")
            .categoryCode("")
            .optionName("")
            .build();
        
        //when
        Double discountedPrice = product.getDiscountedPrice();
        
        //then
        Double targetDiscountedPrice = discountList.stream()
            .reduce(originalPrice,
                (price, discount) -> {
                    double discountAppliedPrice = discount.applyDiscount(price);
                    return Math.max(discountAppliedPrice, 0.0); // Ensure the price does not go below 0
                },
                (price1, price2) -> price2
            );
 
        assertEquals(discountedPrice, targetDiscountedPrice);
    }
}
