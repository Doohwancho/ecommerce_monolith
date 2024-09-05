package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductRepositoryCustom{
    Page<ProductEntity> findAll(Pageable pageable);
}
