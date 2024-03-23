package com.cho.ecommerce.domain.product.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT_OPTION_VARIATION")
@Setter
@Getter
public class ProductOptionVariationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_option_variation_seq")
//    @SequenceGenerator(
//        name = "product_option_variation_seq",
//        sequenceName = "PRODUCT_OPTION_VARIATION_SEQ",
//        allocationSize = 1000
//    )
    @Column(name = "PRODUCT_OPTION_VARIATION_ID")
    private Long productOptionVariationId;
    
    @NotNull(message = "Option variation is required")
    @ManyToOne
    @JoinColumn(name = "OPTION_VARIATION_ID")
    private OptionVariationEntity optionVariation;
    
    @NotNull(message = "Product item is required")
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ITEM_ID")
    private ProductItemEntity productItem;
}
