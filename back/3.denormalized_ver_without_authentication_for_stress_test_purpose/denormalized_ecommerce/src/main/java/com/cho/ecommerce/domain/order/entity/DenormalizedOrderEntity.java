package com.cho.ecommerce.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "DENORMALIZED_ORDER",
    indexes = {
        @Index(name = "idx_order_date", columnList = "ORDER_DATE"),
        @Index(name = "idx_member_id", columnList = "MEMBER_ID"),
        @Index(name = "idx_order_status", columnList = "ORDER_STATUS")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenormalizedOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;
    
    @Column(name = "ORDER_DATE")
    @NotNull(message = "Order date cannot be null")
    private LocalDateTime orderDate;
    
    @Column(name = "ORDER_STATUS")
    @NotBlank(message = "Order status is required")
    private String orderStatus;
    
    @Column(name = "MEMBER_ID")
    @NotNull(message = "Member ID is required")
    private Long memberId;
    
    @Column(name = "MEMBER_NAME")
    @NotBlank(message = "Member name is required")
    private String memberName;
    
    @Column(name = "MEMBER_EMAIL")
    @NotBlank(message = "Member email is required")
    @Email(message = "Invalid email format")
    private String memberEmail;
    
    @Column(name = "TOTAL_PRICE")
    @Min(0)
    private Double totalPrice;
    
    @Column(name = "TOTAL_QUANTITY")
    @Min(0)
    private Integer totalQuantity;
    
    @Column(name = "ORDER_ITEMS", columnDefinition = "JSON")
    @NotNull(message = "Order items cannot be null")
    private String orderItems;
    
    @Column(name = "SHIPPING_ADDRESS", columnDefinition = "JSON")
    private String shippingAddress;
    
    @Column(name = "BILLING_ADDRESS", columnDefinition = "JSON")
    private String billingAddress;
    
    @Column(name = "PAYMENT_INFO", columnDefinition = "JSON")
    private String paymentInfo;
    
    // Additional fields for efficient querying and analytics
    @Column(name = "ITEM_COUNT")
    @Min(1)
    private Integer itemCount;
    
    @Column(name = "HAS_DISCOUNTED_ITEMS")
    private Boolean hasDiscountedItems;
    
    @Column(name = "TOTAL_DISCOUNT")
    @Min(0)
    private Double totalDiscount;
    
    // Business logic methods
    @JsonIgnore
    public boolean isRecentOrder() {
        return LocalDateTime.now().minusDays(7).isBefore(this.orderDate);
    }
    
    @JsonIgnore
    public boolean isHighValueOrder() {
        return this.totalPrice > 1000; // Assuming 1000 is the threshold for high-value orders
    }
    
    // Override toString() for logging
    @Override
    public String toString() {
        return "DenormalizedOrder{" +
            "orderId=" + orderId +
            ", orderDate=" + orderDate +
            ", orderStatus='" + orderStatus + '\'' +
            ", memberId=" + memberId +
            ", memberName='" + memberName + '\'' +
            ", totalPrice=" + totalPrice +
            ", totalQuantity=" + totalQuantity +
            ", itemCount=" + itemCount +
            '}';
    }
}
