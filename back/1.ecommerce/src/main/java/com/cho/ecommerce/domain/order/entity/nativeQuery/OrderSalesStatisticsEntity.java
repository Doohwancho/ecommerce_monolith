package com.cho.ecommerce.domain.order.entity.nativeQuery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderSalesStatisticsEntity {
    private Long categoryId;
    private String categoryName;
    private Integer numberOfProductsPerCategory;
    private Double averageRating;
    private Double totalSalesPerCategory;
    private Long productId;
    private String topSalesProductName;
    private Double topSalesOfProduct;
}
