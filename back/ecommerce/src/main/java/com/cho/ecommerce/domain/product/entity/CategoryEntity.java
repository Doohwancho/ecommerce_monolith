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
@Table(name = "CATEGORY")
public class CategoryEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    
    @Column(name = "CATEGORY_CODE")
    private String categoryCode;
    
    @ManyToOne
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    private CategoryEntity parentCategory;
    
    @OneToMany(mappedBy = "parentCategory")
    private Set<CategoryEntity> subCategories;
    
    @OneToMany(mappedBy = "category")
    private Set<OptionEntity> optionEntities;
    
    @OneToMany(mappedBy = "category")
    private Set<ProductEntity> products;
    
    // getters, setters, and other methods
}
