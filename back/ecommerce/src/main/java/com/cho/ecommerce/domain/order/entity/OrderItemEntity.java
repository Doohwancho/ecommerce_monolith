package com.cho.ecommerce.domain.order.entity;

import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ORDER_ITEM")
public class OrderItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;
    
    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;
    
    @ManyToOne
    @JoinColumn(name = "PRODUCT_OPTION_VARIATION_ID")
    private ProductOptionVariationEntity productOptionVariation;
    
}
