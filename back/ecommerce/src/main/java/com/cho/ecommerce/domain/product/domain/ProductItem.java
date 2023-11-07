package com.cho.ecommerce.domain.product.domain;

import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
public class ProductItem {
    private Product product;
    private Integer quantity;
    private Double price;
    private Set<ProductOptionVariationEntity> productOptionVariations;
    private Discount discount;
    
    public ProductItem(final Double price, final int quantity, final Discount discount) {
        Assert.isTrue(price > 0, "상품 가격은 0보다 커야 합니다.");
        Assert.isTrue(quantity > 0, "상품 수량은 0보다 커야 합니다.");
        Assert.notNull(discount, "할인 정책은 필수입니다.");
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }
    
    public void update(final Double price, final int quantity, final Discount discount) {
        Assert.isTrue(price > 0, "상품 가격은 0보다 커야 합니다.");
        Assert.isTrue(quantity > 0, "상품 수량은 0보다 커야 합니다.");
        Assert.notNull(discount, "할인 정책은 필수입니다.");
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }
    
    public Double getDiscountedPrice() {
        return discount.applyDiscount(price);
    }
    
}