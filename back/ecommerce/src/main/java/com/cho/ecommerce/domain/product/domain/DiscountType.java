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
    
    public static DiscountType fromDescription(String description) {
        for (DiscountType type : values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
            "No DiscountType with description " + description + " found");
    }
}