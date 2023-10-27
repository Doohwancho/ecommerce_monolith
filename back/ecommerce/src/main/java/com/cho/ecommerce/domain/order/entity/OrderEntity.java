package com.cho.ecommerce.domain.order.entity;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.product.domain.Product;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class OrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;
    
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate; //TODO - OffsetDateTime에서 Offset 저장 지원 안되는 db있으니, LocalDateTime랑 Offset까지 별개 column으로 저장해야 함
    
    @Column(name = "ORDER_STATUS")
    private String orderStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private UserEntity member;
    
    @OneToMany(mappedBy = "order")
    private Set<OrderItemEntity> orderItems;
    
    public OrderEntity(final UserEntity member, final OrderItemEntity orderItems) {
        Assert.notNull(member, "주문자는 필수입니다.");
        Assert.notNull(orderItems, "주문 정보가 필요합니다.");
        this.member = member;
        this.orderItems.add(orderItems);
    }
 
}
