package com.cho.ecommerce.domain.order.entity;

import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDER_ITEM")
@Getter
@Setter
public class OrderItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
//    @SequenceGenerator(
//        name = "order_item_seq",
//        sequenceName = "ORDER_ITEM_SEQ",
//        allocationSize = 1000
//    )
    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;
    
    @Min(0)
    @Column(name = "QUANTITY", length = DatabaseConstants.PRODUCT_ITEM_QUANTITY_SIZE)
    private Integer quantity;
    
    @Min(0)
    @Column(name = "PRICE", length = DatabaseConstants.PRODUCT_ITEM_PRICE_SIZE)
    private Double price;
    
    //@NotNull //TODO - required?
    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;
    
    //@NotNull //TODO - required?
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRODUCT_OPTION_VARIATION_ID")
    private ProductOptionVariationEntity productOptionVariation;
    
}
