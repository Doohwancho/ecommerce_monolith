package com.cho.ecommerce.domain.product.domain;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;

@ToString
@Setter
@Getter
public class Product {
    
    //product
    private Long productId;
    private String name;
    private String description;
    private Double rating;
    private Integer ratingCount;
    
    private Integer quantity;
    private Double price;
    private List<Discount> discounts;
    
    //category
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    
    //option
    private Long optionId;
    private String optionName;
    private Long optionVariationId;
    private String optionVariationName;
    
    
    public void update(final Double price, final int quantity, final List<Discount> discounts) {
        Assert.isTrue(price >= 0, "상품 가격은 음수일 수 없습니다.");
        Assert.isTrue(quantity >= 0, "상품 수량은 음수일 수 없습니다.");
        this.price = price;
        this.quantity = quantity;
        this.discounts = discounts;
    }
    
    public Double getDiscountedPrice() {
        Double discountedPrice = price;
        
        for (Discount discount : discounts) {
            discountedPrice = discount.applyDiscount(discountedPrice);
        }
        discountedPrice = Math.max(discountedPrice, 0);
        
        return discountedPrice;
    }
    
    
    // Static inner Builder class
    public static class Builder {
        
        private Long productId;
        private String name;
        private String description;
        private Double rating;
        private Integer ratingCount;
        private Integer quantity;
        private Double price;
        private List<Discount> discounts;
        private Long categoryId;
        private String categoryCode;
        private String categoryName;
        private Long optionId;
        private String optionName;
        private Long optionVariationId;
        private String optionVariationName;
        
        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder rating(Double rating) {
            this.rating = rating;
            return this;
        }
        
        public Builder ratingCount(Integer ratingCount) {
            this.ratingCount = ratingCount;
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
        
        public Builder discounts(List<Discount> discounts) {
            this.discounts = discounts;
            return this;
        }
        
        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }
        
        public Builder categoryCode(String categoryCode) {
            this.categoryCode = categoryCode;
            return this;
        }
        
        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }
        
        public Builder optionId(Long optionId) {
            this.optionId = optionId;
            return this;
        }
        
        public Builder optionName(String optionName) {
            this.optionName = optionName;
            return this;
        }
        
        public Builder optionVariationId(Long optionVariationId) {
            this.optionVariationId = optionVariationId;
            return this;
        }
        
        public Builder optionVariationName(String optionVariationName) {
            this.optionVariationName = optionVariationName;
            return this;
        }
        
        public Product build() {
            Assert.isTrue(price >= 0, "상품 가격은 음수가 될 수 없습니다.");
            Assert.isTrue(quantity >= 0, "상품 수량은 음수가 될 수 없습니다.");
            Assert.notNull(name, "상품 이름은 null이 될 수 없습니다.");
            Assert.notNull(categoryId, "카테고리 Id는 null이 될 수 없습니다.");
            Assert.notNull(categoryCode, "카테고리 코드는 null이 될 수 없습니다.");
            Assert.notNull(categoryName, "카테고리 이름은 null이 될 수 없습니다.");
            Assert.notNull(optionId, "옵션 아이디는 null이 될 수 없습니다.");
            Assert.notNull(optionName, "옵션 이름은 null이 될 수 없습니다.");
            
            Product product = new Product();
            product.productId = this.productId;
            product.name = this.name;
            product.description = this.description;
            product.rating = this.rating;
            product.ratingCount = this.ratingCount;
            product.quantity = this.quantity;
            product.price = this.price;
            product.discounts = this.discounts;
            product.categoryId = this.categoryId;
            product.categoryCode = this.categoryCode;
            product.categoryName = this.categoryName;
            product.optionId = this.optionId;
            product.optionName = this.optionName;
            product.optionVariationId = this.optionVariationId;
            product.optionVariationName = this.optionVariationName;
            return product;
        }
    }
}

