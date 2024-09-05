package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.global.config.database.DatabaseConstants;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OPTION_VARIATION")
@Getter
@Setter
public class OptionVariationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_variation_seq")
//    @SequenceGenerator(
//        name = "option_variation_seq",
//        sequenceName = "OPTION_VARIATION_SEQ",
//        allocationSize = 1000
//    )
    @Column(name = "OPTION_VARIATION_ID")
    private Long optionVariationId;
    
    @NotBlank(message = "Value is required")
    @Column(name = "VALUE", length = DatabaseConstants.OPTION_VARIATION_VALUE_SIZE)
    private String value;
    
    @NotNull(message = "Option is required")
    @ManyToOne
    @JoinColumn(name = "OPTION_ID")
    private OptionEntity option;
    
    //    @NotEmpty(message = "Option variation must have at least one product option variation") //optionVariation may not have product option variations
    @OneToMany(mappedBy = "optionVariation")
    private Set<ProductOptionVariationEntity> productOptionVariations;
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void setOption(OptionEntity option) {
        this.option = option;
    }
}
