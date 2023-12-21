package com.cho.ecommerce.domain.product.domain;

import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@NoArgsConstructor
@Getter
@Setter
public class ProductItem {
    
    private Long productItemId;
    private Product product;
    private Integer quantity;
    private Double price;
    private Set<ProductOptionVariationEntity> productOptionVariations;
    private Discount discount;
    
    public void update(final Double price, final int quantity, final Discount discount) {
        Assert.isTrue(price > 0, "상품 가격은 0보다 커야 합니다.");
        Assert.isTrue(quantity > 0, "상품 수량은 0보다 커야 합니다.");
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }
    
    public Double getDiscountedPrice() {
        return discount.applyDiscount(price);
    }
    
    // Static Builder class
    public static class Builder {
        private Long productItemId;
        private Product product;
        private Integer quantity;
        private Double price;
        private Set<ProductOptionVariationEntity> productOptionVariations;
        private Discount discount;
        
        public Builder id(Long id){
            this.productItemId = id;
            return this;
        }
        public Builder product(Product product) {
            this.product = product;
            return this;
        }
        
        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public Builder price(Double price) {
            this.price = price;
            return this;
        }
        
        public Builder productOptionVariations(Set<ProductOptionVariationEntity> productOptionVariations) {
            this.productOptionVariations = productOptionVariations;
            return this;
        }
        
        public Builder discount(Discount discount) {
            this.discount = discount;
            return this;
        }
        
        public ProductItem build() {
            ProductItem productItem = new ProductItem();
            Assert.isTrue(price > 0, "상품 가격은 0보다 커야 합니다.");
            Assert.isTrue(quantity > 0, "상품 수량은 0보다 커야 합니다.");
            
            productItem.productItemId = this.productItemId;
            productItem.product = this.product;
            productItem.quantity = this.quantity;
            productItem.price = this.price;
            productItem.productOptionVariations = this.productOptionVariations;
            productItem.discount = this.discount;
            
            return productItem;
        }
    }
}