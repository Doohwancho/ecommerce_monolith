package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.util.Set;
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
    @Column(name = "NAME", length = DatabaseConstants.PRODUCT_NAME_SIZE)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Column(name = "DESCRIPTION", length = DatabaseConstants.PRODUCT_DESCRIPTION_SIZE)
    private String description;
    
    @NotBlank(message = "rating is required")
    @Column(name = "RATING", length = DatabaseConstants.PRODUCT_RATING_SIZE)
    private Double rating;
    
    @NotBlank(message = "rating count is required")
    @Column(name = "RATING_COUNT", length = DatabaseConstants.PRODUCT_RATING_COUNT_SIZE)
    private Integer ratingCount;
    
    @NotNull(message = "Category is required")
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;
    
//    @NotEmpty(message = "Product must have at least one item") //product may not have product-items
//    @OneToMany(mappedBy = "product")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductItemEntity> productItems;
}
