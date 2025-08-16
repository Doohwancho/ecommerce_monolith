package com.ecommerce.monolith.domain.order.dto;

import com.ecommerce.monolith.domain.order.domain.Order.OrderItem;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsResponseDTO {
    private Long orderId;
    private OffsetDateTime orderDate;
    private String orderStatus;
    private String memberName;
    private List<OrderItem> orderItems;
    private Double totalPrice;
    private Integer totalQuantity;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
