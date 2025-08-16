package com.ecommerce.monolith.domain.order.dto;

import com.ecommerce.monolith.domain.order.domain.Order.OrderItem;
import java.util.List;
import lombok.Data;

@Data
public class CreateOrderRequestDTO {
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private List<OrderItem> orderItems;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
