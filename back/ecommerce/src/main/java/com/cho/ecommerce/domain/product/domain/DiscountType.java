package com.cho.ecommerce.domain.product.domain;

public enum DiscountType {
    PERCENTAGE("Percentage"),
    FLAT_RATE("Flat Rate");
    
    private final String description;
    
    DiscountType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
}