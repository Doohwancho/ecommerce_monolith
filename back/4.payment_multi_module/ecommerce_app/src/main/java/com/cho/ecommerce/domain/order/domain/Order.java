package com.cho.ecommerce.domain.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
public class Order {
    private Long orderId;
    private OffsetDateTime orderDate;
    private String orderStatus;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private List<OrderItem> orderItems;
    private Double totalPrice;
    private Integer totalQuantity;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    @JsonIgnoreProperties
    @Data
    public static class OrderItem {
        public OrderItem() {}
        @JsonProperty("productName")
        private String productName;
        @JsonProperty("quantity")
        private int quantity;
        @JsonProperty("basePrice")
        private double basePrice;
        @JsonProperty("discountedPrice")
        private double discountedPrice;
    }
}
