package com.cho.ecommerce.domain.product.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product{
    
    private Long productId;
    private String name;
    private String description;
    private Double rating;
    private Integer ratingCount;
}

