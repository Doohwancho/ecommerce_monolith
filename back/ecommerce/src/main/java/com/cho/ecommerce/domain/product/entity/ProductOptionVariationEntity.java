package com.cho.ecommerce.domain.product.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT_OPTION_VARIATION")
@Setter
public class ProductOptionVariationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_OPTION_VARIATION_ID")
    private Long productOptionVariationId;
    
    @ManyToOne
    @JoinColumn(name = "OPTION_VARIATION_ID")
    private OptionVariationEntity optionVariation;
    
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ITEM_ID")
    private ProductItemEntity productItem;
    
    // getters, setters, and other methods
}
