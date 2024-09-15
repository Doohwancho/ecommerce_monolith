package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.entity.nativeQuery.OrderSalesStatisticsInterface;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, OrderRepositoryCustom {
    
    /**
     * 통계쿼리)
     *
     * 23년 6월 ~ 23년 12월 사이에
     * 카테고리 별 상품 갯수
     * 해당 카테고리의 상품들의 평균 평점
     * 해당 카테고리의 총 상품 판매액
     * 해당 카테고리에서 가장 많이 팔린 상품의 productId
     * 해당 카테고리에서 가장 많이 팔린 상품의 이름
     * 해당 카테고리에서 가장 많이 팔린 상품의 총 판매액
     * ...을 쿼리한다.
     *
     * @return
     * 1. categoryId
     * 2. categoryName
     * 3. numberOfProductsPerCategory
     * 4. AverageRating
     * 5. TotalSalesPerCategory
     * 6. ProductId
     * 7. TopSalesProduct
     * 8. TopSalesOfProduct
     */
    @Transactional
    @Query(
        value = "SELECT\n"
            + "\ttmp1.CategoryId AS categoryId,\n"
            + "    tmp1.CategoryName AS categoryName,\n"
            + "    tmp1.NumberOfProductsPerCategory AS numberOfProductsPerCategory,\n"
            + "    tmp1.AverageRating AS averageRating,\n"
            + "    tmp1.TotalSalesPerCategory AS totalSalesPerCategory,\n"
            + "    tmp2.ProductId AS productId,\n"
            + "    tmp2.ProductName AS topSalesProductName,\n"
            + "    tmp2.TopSalesOfProduct AS topSalesOfProduct\n"
            + "FROM (\n"
            + "\tSELECT \n"
            + "\t\tc.CATEGORY_ID AS CategoryId,\n"
            + "\t\tc.NAME AS CategoryName,\n"
            + "\t\tCOUNT(DISTINCT p.PRODUCT_ID) AS NumberOfProductsPerCategory,\n"
            + "\t\tROUND(AVG(p.RATING), 1) AS AverageRating,\n"
            + "\t\tROUND(SUM(pi.Quantity * pi.PRICE), 1) AS TotalSalesPerCategory\n"
            + "\tFROM CATEGORY c\n"
            + "\tJOIN PRODUCT p ON c.CATEGORY_ID = p.CATEGORY_ID\n"
            + "\tJOIN PRODUCT_ITEM pi ON p.PRODUCT_ID = pi.PRODUCT_ID\n"
            + "\tJOIN PRODUCT_OPTION_VARIATION pov ON pi.PRODUCT_ITEM_ID = pov.PRODUCT_ITEM_ID\n"
            + "\tJOIN ORDER_ITEM oi ON pov.PRODUCT_OPTION_VARIATION_ID = oi.PRODUCT_OPTION_VARIATION_ID\n"
            + "\tJOIN `ORDER` o USE INDEX (idx_order_date) ON oi.ORDER_ID = o.ORDER_ID\n"
//            + "\tWHERE o.ORDER_DATE BETWEEN '2024-07-01' AND '2024-09-31'\n"
            + "\tWHERE o.ORDER_DATE BETWEEN :startDate AND :endDate\n"
            + "\tGROUP BY c.CATEGORY_ID\n"
            + ") AS tmp1\n"
            + "JOIN\n"
            + "\t(\n"
            + "\tSELECT\n"
            + "\t\ta.CategoryId AS CategoryId,\n"
            + "\t--     a.CategoryName,\n"
            + "\t\tb.ProductId As ProductId,\n"
            + "\t\tb.ProductName As ProductName,\n"
            + "\t\ta.TopSalesOfProduct AS TopSalesOfProduct\n"
            + "\tFROM\n"
            + "\t\t(SELECT \n"
            + "\t\t\tSub.CategoryId,\n"
            + "\t\t\tSub.CategoryName,\n"
            + "\t\t\tMAX(Sub.TotalSalesPerProduct) as TopSalesOfProduct\n"
            + "\t\tFROM\n"
            + "\t\t\t(SELECT \n"
            + "\t\t\t\tc.CATEGORY_ID as CategoryId,\n"
            + "\t\t\t\tc.name as CategoryName,\n"
            + "\t\t\t\tp2.PRODUCT_ID,\n"
            + "\t\t\t\tROUND(SUM(pi2.Quantity * pi2.PRICE), 1) as TotalSalesPerProduct\n"
            + "\t\t\tFROM CATEGORY c\n"
            + "\t\t\tJOIN PRODUCT p2 ON c.CATEGORY_ID = p2.CATEGORY_ID\n"
            + "\t\t\tINNER JOIN PRODUCT_ITEM pi2 ON p2.PRODUCT_ID = pi2.PRODUCT_ID\n"
            + "\t\t\tINNER JOIN PRODUCT_OPTION_VARIATION pov2 ON pi2.PRODUCT_ITEM_ID = pov2.PRODUCT_ITEM_ID\n"
            + "\t\t\tINNER JOIN ORDER_ITEM oi2 ON pov2.PRODUCT_OPTION_VARIATION_ID = oi2.PRODUCT_OPTION_VARIATION_ID\n"
            + "\t\t\tINNER JOIN `ORDER` o2 USE INDEX (idx_order_date) ON oi2.ORDER_ID = o2.ORDER_ID\n"
//            + "\t\t\tWHERE o2.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'\n"
            + "\t\t\tWHERE o2.ORDER_DATE BETWEEN :startDate AND :endDate\n"
            + "\t\t\tGROUP BY c.CATEGORY_ID, p2.PRODUCT_ID\n"
            + "\t\t\t) as Sub\n"
            + "\t\tGROUP BY Sub.CategoryId\n"
            + "\t\t) a\n"
            + "\tJOIN\n"
            + "\t\t(SELECT \n"
            + "\t\t\tc.CATEGORY_ID as CategoryId,\n"
            + "\t\t\tc.name as CategoryName,\n"
            + "\t\t\tp2.PRODUCT_ID as ProductId,\n"
            + "\t\t\tp2.name as ProductName,\n"
            + "\t\t\tROUND(SUM(pi2.Quantity * pi2.PRICE), 1) as TopSalesOfProduct\n"
            + "\t\tFROM CATEGORY c\n"
            + "\t\tJOIN PRODUCT p2 ON c.CATEGORY_ID = p2.CATEGORY_ID\n"
            + "\t\tINNER JOIN PRODUCT_ITEM pi2 ON p2.PRODUCT_ID = pi2.PRODUCT_ID\n"
            + "\t\tINNER JOIN PRODUCT_OPTION_VARIATION pov2 ON pi2.PRODUCT_ITEM_ID = pov2.PRODUCT_ITEM_ID\n"
            + "\t\tINNER JOIN ORDER_ITEM oi2 ON pov2.PRODUCT_OPTION_VARIATION_ID = oi2.PRODUCT_OPTION_VARIATION_ID\n"
            + "\t\tINNER JOIN `ORDER` o2 USE INDEX (idx_order_date) ON oi2.ORDER_ID = o2.ORDER_ID\n"
//            + "\t\tWHERE o2.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'\n"
            + "\t\tWHERE o2.ORDER_DATE BETWEEN :startDate AND :endDate\n"
            + "\t\tGROUP BY c.CATEGORY_ID, p2.PRODUCT_ID\n"
            + "\t\t\t) b\n"
            + "\t\tON a.CategoryId = b.CategoryId AND a.TopSalesOfProduct = b.TopSalesOfProduct\n"
            + "\t\tORDER BY a.CategoryId\n"
            + "\t) AS tmp2\n"
            + "ON tmp1.CategoryId = tmp2.CategoryId\n"
    , nativeQuery = true)
    List<OrderSalesStatisticsInterface> findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringLastNMonths(@Param("startDate") String startDate,
        @Param("endDate") String endDate);
    //TODO - native query에서 vectic(`)이 잘 작동하는데, 아래의 startDate, endDate를 동적으로 넣으려면 jpql을 써야하는데, jpql에서는 vectic이 안먹힘. 결국 Mysql의 vectic을 써야만 하는 테이블 명(ex. ORDER)을 바꿔야 할 듯 하다.
    //@Param("startDate") String startDate, @Param("endDate") String endDate
    
    //subquery가 여럿 있는데, queryDSL은 subquery를 안티패턴이라고 지원 안한다. in subquery로 치환하면 만들 순 있지만, in subquery의 성능이 매우 안좋으므로, querydsl로 바꾸는건 재고려 하자.
}
