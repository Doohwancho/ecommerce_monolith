package com.cho.ecommerce.domain.order.entity.nativeQuery;

public interface OrderSalesStatisticsInterface {
    Long getCategoryId();
    String getCategoryName();
    Integer getNumberOfProductsPerCategory();
    Double getAverageRating();
    Double getTotalSalesPerCategory();
    Long getProductId();
    String getTopSalesProductName();
    Double getTopSalesOfProduct();
}
