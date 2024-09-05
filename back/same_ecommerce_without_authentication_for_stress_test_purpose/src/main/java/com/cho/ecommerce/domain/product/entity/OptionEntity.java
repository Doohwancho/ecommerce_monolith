package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.util.List;
import javax.persistence.CascadeType;
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
@Table(name = "`OPTION`")
//mysql에 OPTION 이름의 테이블 생성 불가라 `OPTION`으로 적었지만, h2에서는 "OPTION"으로 생성되어 문제가 생긴다.
@Getter
@Setter
public class OptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_seq")
//    @SequenceGenerator(
//        name = "option_seq",
//        sequenceName = "OPTION_SEQ",
//        allocationSize = 1000
//    )
    @Column(name = "OPTION_ID")
    private Long optionId;
    
    @NotBlank(message = "Value is required")
    @Column(name = "VALUE", length = DatabaseConstants.OPTION_VALUE_SIZE)
    private String value;
    
    @NotNull(message = "Category is required")
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;
    
    //    @NotEmpty(message = "Option must have at least one variation") //option may not have option variations
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
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