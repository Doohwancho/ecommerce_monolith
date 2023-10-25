package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.domain.product.domain.DiscountType;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "DISCOUNT")
public class DiscountEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNT_ID")
    private Long discountId;
    
    @Enumerated(EnumType.STRING) //String 형태로 "Percentage", "Flat Rate"로 저장된다.
    @Column(name = "DISCOUNT_TYPE", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "DISCOUNT_VALUE", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "START_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Column(name = "END_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ITEM_ID") //PRODUCT_ITEM_ID column이 대신 생성된다.
    private ProductItemEntity productItem;
    
    
    
    
    public ProductItemEntity getProductItem() {
        return productItem;
    }
    
    public void setProductItem(ProductItemEntity productItem) {
        this.productItem = productItem;
    }
    
    public DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
}