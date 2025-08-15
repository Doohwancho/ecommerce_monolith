package com.cho.ecommerce.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedDTO {
    private Long orderId;
    private String orderStatus;
}

