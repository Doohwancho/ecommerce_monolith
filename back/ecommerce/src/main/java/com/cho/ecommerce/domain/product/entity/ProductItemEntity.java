package com.cho.ecommerce.domain.product.entity;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PRODUCT_ITEM")
public class ProductItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ITEM_ID")
    private Long productItemId;
    
    @Column(name = "QUANTITY")
    private Integer quantity;
    
    @Column(name = "PRICE")
    private Double price;
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private ProductEntity product;
    
    @OneToMany(mappedBy = "productItem")
    private Set<ProductOptionVariationEntity> productOptionVariations;
    
    // getters, setters, and other methods
}

