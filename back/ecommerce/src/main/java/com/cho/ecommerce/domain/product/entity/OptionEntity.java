package com.cho.ecommerce.domain.product.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`OPTION`")
@Getter
@Setter
public class OptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_ID")
    private Long optionId;
    
    @NotBlank(message = "Value is required")
    @Column(name = "VALUE")
    private String value;
    
    @NotNull(message = "Category is required")
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;
    
//    @NotEmpty(message = "Option must have at least one variation") //option may not have option variations
    @OneToMany(mappedBy = "option")
    private List<OptionVariationEntity> optionVariations;
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public List<OptionVariationEntity> getOptionVariations() {
        return this.optionVariations;
    }
    
    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}