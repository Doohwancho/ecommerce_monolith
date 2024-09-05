package com.cho.ecommerce.domain.product.entity;

import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.util.ArrayList;
import java.util.List;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT_ITEM")
@Setter
@Getter
public class ProductItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_item_seq")
//    @SequenceGenerator(
//        name = "product_item_seq",
//        sequenceName = "PRODUCT_ITEM_SEQ",
//        allocationSize = 1000
//    )
    @Column(name = "PRODUCT_ITEM_ID")
    private Long productItemId;
    
    @Min(0)
    @Column(name = "QUANTITY", length = DatabaseConstants.PRODUCT_ITEM_QUANTITY_SIZE)
    private Integer quantity;
    
    @Min(0)
    @Column(name = "PRICE", length = DatabaseConstants.PRODUCT_ITEM_PRICE_SIZE)
    private Double price;
    
    @NotNull(message = "Product is required")
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private ProductEntity product;
    
    //    @NotEmpty(message = "Product item must have at least one product option variation") //TODO - ProductOptionVariationEntity가 ProductItemEntity를 @NotNull 해놨는데, 여기서도 양방향으로 @NotEmpty 걸면 어떻게 두 객체를 동시에 validation 어기지 않고 insert 하지?
    @OneToMany(mappedBy = "productItem", cascade = CascadeType.ALL, orphanRemoval = true)
    //TODO - CascadeType.ALL 말고 뭘 쓸지 고려하기
    private Set<ProductOptionVariationEntity> productOptionVariations;
    
    
    //    @NotEmpty(message = "Product item must have at least one discount") //product may not have discount
    @OneToMany(mappedBy = "productItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountEntity> discounts = new ArrayList<>();
    
    public void addDiscount(DiscountEntity discount) {
        discounts.add(discount);
        discount.setProductItem(this);
    }
    
    public void removeDiscount(DiscountEntity discount) {
        discounts.remove(discount);
        discount.setProductItem(null);
    }
    
    public void setDiscounts(List<DiscountEntity> discounts) {
        this.discounts.clear();
        if (discounts != null) {
            discounts.forEach(this::addDiscount);
        }
    }
    
    public void addProductOptionVariation(ProductOptionVariationEntity variation) {
        productOptionVariations.add(variation);
        variation.setProductItem(this);
    }
    
    public void removeProductOptionVariation(ProductOptionVariationEntity variation) {
        productOptionVariations.remove(variation);
        variation.setProductItem(null);
    }
    
    public void setProductOptionVariations(Set<ProductOptionVariationEntity> variations) {
        this.productOptionVariations.clear();
        if (variations != null) {
            variations.forEach(this::addProductOptionVariation);
        }
    }
}

