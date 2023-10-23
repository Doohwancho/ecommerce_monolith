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
@Table(name = "OPTION_VARIATION")
public class OptionVariationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_VARIATION_ID")
    private Long optionVariationId;
    
    @Column(name = "VALUE")
    private String value;
    
    @ManyToOne
    @JoinColumn(name = "OPTION_ID")
    private OptionEntity option;
    
    @OneToMany(mappedBy = "optionVariation")
    private Set<ProductOptionVariationEntity> productOptionVariations;
    
    // getters, setters, and other methods
}
