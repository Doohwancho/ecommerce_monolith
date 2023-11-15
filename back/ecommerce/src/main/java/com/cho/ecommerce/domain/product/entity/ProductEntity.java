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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT")
@Setter
@Getter
public class ProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;
    
    @NotBlank(message = "Name is required")
    @Column(name = "NAME")
    private String name;
    
    @NotBlank(message = "Description is required")
    @Column(name = "DESCRIPTION")
    private String description;
    
    @NotBlank(message = "rating is required")
    @Column(name = "RATING")
    private Double rating;
    
    @NotBlank(message = "rating count is required")
    @Column(name = "RATING_COUNT")
    private Integer ratingCount;
    
    @NotNull(message = "Category is required")
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;
    
//    @NotEmpty(message = "Product must have at least one item") //product may not have product-items
    @OneToMany(mappedBy = "product")
    private Set<ProductItemEntity> productItems;
    
    // getters, setters, and other methods
}
