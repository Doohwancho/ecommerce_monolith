package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import com.cho.ecommerce.domain.product.entity.DenormalizedProductEntity;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<DenormalizedProductEntity, Long> { //, ProductRepositoryCustom{
    Page<DenormalizedProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM DenormalizedProductEntity p WHERE p.name= :productname")
    DenormalizedProductEntity findProductsByName(@Param("productname") String productname);
    
    @Cacheable(value = "topTenRatedProductsCached", key = "#pageable.pageSize")
    @Query("SELECT p FROM DenormalizedProductEntity p WHERE p.ratingCount > 0 ORDER BY p.rating DESC")
    List<DenormalizedProductEntity> findTopTenRatedProducts(Pageable pageable);
}
