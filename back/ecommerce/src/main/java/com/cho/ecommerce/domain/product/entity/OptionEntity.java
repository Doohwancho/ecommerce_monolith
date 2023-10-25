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
@Table(name = "OPTION")
public class OptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_ID")
    private Long optionId;
    
    @Column(name = "VALUE")
    private String value;
    
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;
    
    @OneToMany(mappedBy = "option")
    private Set<OptionVariationEntity> optionVariations;
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public Set<OptionVariationEntity> getOptionVariations() {
        return this.optionVariations;
    }
    
    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}