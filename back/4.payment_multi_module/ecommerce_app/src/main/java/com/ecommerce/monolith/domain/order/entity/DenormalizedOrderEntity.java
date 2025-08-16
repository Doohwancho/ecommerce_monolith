package com.ecommerce.monolith.domain.order.entity;

import com.ecommerce.monolith.domain.order.domain.Order;
import com.ecommerce.monolith.global.config.parser.ObjectMapperUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Id;
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
        @Index(name = "IDX_ORDER_DATE", columnList = "ORDER_DATE"), //order query시 기간을 where절에 조건으로 넣는 경우가 많음
        @Index(name = "IDX_MEMBER_NAME", columnList = "MEMBER_NAME"), //username으로 orders 찾는 쿼리에 쓰임
//        @Index(name = "IDX_MEMBER_ID", columnList = "MEMBER_ID"),
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
    private OffsetDateTime orderDate;
    
    @Column(name = "ORDER_STATUS")
    @NotBlank(message = "Order status is required")
    private String orderStatus;
    
    //Q. JPA 관계 설정 with DenormalizedMemberEntity 묶어줘야 하나?
    @Column(name = "MEMBER_ID")
    @NotNull(message = "Member ID is required")
    private Long memberId;
    
    @Column(name = "MEMBER_NAME")
    @NotBlank(message = "Member name is required")
    private String memberName;
    
    @Column(name = "MEMBER_EMAIL")
    @NotBlank(message = "Member email is required")
//    @Email(message = "Invalid email format")
    private String memberEmail;
    
    @Column(name = "TOTAL_PRICE")
    @Min(0)
    private Double totalPrice;
    
    @Column(name = "TOTAL_QUANTITY")
    @Min(0)
    private Integer totalQuantity;
    
    //Q. JSON으로 한다면, 어떤 structure 여야 하지?
    // [
    //  {
    //      "productName": {product_name},
    //      "quantity": {quantity},
    //      "basePrice": ${base_price},
    //      "discountedPrice": {discounted_price}
    //  },
    //  ...
    // ]
    @Column(name = "ORDER_ITEMS", columnDefinition = "JSON")
    @NotNull(message = "Order items cannot be null")
    private String orderItems;
    
    @Column(name = "STREET")
    @NotBlank(message = "Street is required")
    private String street;
    
    @Column(name = "CITY")
    @NotBlank(message = "City is required")
    private String city;
    
    @Column(name = "STATE")
    @NotBlank(message = "State is required")
    private String state;
    
    @Column(name = "COUNTRY")
    @NotBlank(message = "Country is required")
    private String country;
    
    @Column(name = "ZIP_CODE")
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    
    //yet
//    @Column(name = "PAYMENT_INFO", columnDefinition = "JSON")
//    private String paymentInfo;
    
    // Additional fields for efficient querying and analytics
    
//    @ManyToOne
//    @JoinColumn(name = "MEMBER_ID", nullable = false)
//    private DenormalizedMemberEntity member;
    
    // Business logic methods
    @JsonIgnore
    public boolean isRecentOrder() {
        return LocalDateTime.now().minusDays(7).isBefore(this.orderDate.toLocalDateTime());
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
            '}';
    }
    
    /*****************************************************
     * Json fields methods
     */
    private static ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getObjectMapper();
    }
    
    /*****************************************************
     * json option method
     */
    public List<Order.OrderItem> getOrderItemsAsList() throws IOException {
        if (this.orderItems == null || this.orderItems.isEmpty()) {
            return new ArrayList<>();
        }
        //주의! 이 처리를 미리 해주지 않으면, objectMapper.readValue()에서 파싱 에러난다!
        //database에서 read 해왔을 때는 ""[{\"name\":"hello\", ...
        //여기서 맨 앞에 "와 중간에 섞인 \, backspace 때문에 objectMapper.readValue() 시 에러난다.
        //따라서 이 둘을 먼저 없애 준 후 파싱해야 한다.
        String jsonString = this.orderItems;
        if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }
        // Unescape the inner quotes
        jsonString = jsonString.replace("\\\"", "\"");
//        log.info("Cleaned JSON string: " + jsonString);
        return getObjectMapper().readValue(jsonString, new TypeReference<List<Order.OrderItem>>(){});
    }
    
    public void setOrderItemsFromList(List<Order.OrderItem> orderItemList) throws IOException {
        this.orderItems = getObjectMapper().writeValueAsString(orderItemList);
    }
    
    public static String convertToJsonFromOrderItemList(List<Order.OrderItem> orderItemList) throws IOException {
        return getObjectMapper().writeValueAsString(orderItemList);
    }
}
