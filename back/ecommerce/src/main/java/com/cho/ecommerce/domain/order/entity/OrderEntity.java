package com.cho.ecommerce.domain.order.entity;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`ORDER`") //mysql에 ORDER 이름의 테이블 생성 불가라 `ORDER`으로 적었지만, h2에서는 "ORDER"으로 생성되어 문제가 생긴다.
@Getter
@Setter
public class OrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;
    
    @Column(name = "ORDER_DATE")
    @NotNull(message = "Order date cannot be null")
    private LocalDateTime orderDate; //TODO - OffsetDateTime에서 Offset 저장 지원 안되는 db있으니, LocalDateTime랑 Offset까지 별개 column으로 저장해야 함
    
    @Column(name = "ORDER_STATUS", length = DatabaseConstants.ORDER_STATUS_SIZE)
    @NotBlank(message = "Order status is required")
    private String orderStatus; //PENDING, PROCESSING, CONFIRMED, PAID, ON_HOLD, SHIPPED, OUT_OF_DELIVERY, etc
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private UserEntity member;
    
    @OneToMany(mappedBy = "order")
    @NotEmpty(message = "Order must have at least one item")
    private Set<OrderItemEntity> orderItems;
    
}
