package com.cho.ecommerce.domain.order.entity;

import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDER_ITEM")
@Getter
@Setter
public class OrderItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;
    
    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRODUCT_OPTION_VARIATION_ID")
    private ProductOptionVariationEntity productOptionVariation;
    
}
