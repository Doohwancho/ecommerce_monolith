package com.cho.ecommerce.domain.order.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class OrderItemDetails {
    private Long orderItemId;
    private Long orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private Long memberId;
    private String username;
    private String email;
    private String name;
    private String role;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long productId;
    private String productName;
    private String description;
    private Double rating;
    private Integer ratingCount;
    private String optionValue;
    private String optionVariationValue;
    private Integer quantity;
    private Double price;
}

