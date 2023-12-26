package com.cho.ecommerce.domain.order.entity;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "CART")
@Setter
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long cartId;
    
    @Column(name = "ADDED_DATE")
    @NotNull(message = "added date cannot be null")
    private LocalDateTime addedDate; //TODO - OffsetDateTime에서 Offset 저장 지원 안되는 db있으니, LocalDateTime랑 Offset까지 별개 column으로 저장해야 함
    
    @Min(0)
    @Column(name = "QUANTITY", length = DatabaseConstants.CART_ITEM_QUANTITY_SIZE)
    private Integer quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private UserEntity member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_OPTION_VARIATION_ID")
    private ProductOptionVariationEntity productOptionVariation;
}
