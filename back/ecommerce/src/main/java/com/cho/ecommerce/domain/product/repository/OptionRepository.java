package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptionRepository extends JpaRepository<OptionEntity, Long>, OptionRepositoryCustom{

//    @Query("SELECT o FROM OptionEntity o WHERE o.category.categoryId = :categoryId")
//    List<OptionEntity> findByCategory_CategoryId(@Param("categoryId") Long categoryId);
}
