package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionVariationEntity;
import com.cho.ecommerce.domain.product.mapper.CategoryMapper;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class OptionVariationRepositoryImpl implements OptionVariationRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    private final CategoryMapper categoryMapper;
    
    @Autowired
    public OptionVariationRepositoryImpl(EntityManager entityManager,
        CategoryMapper categoryMapper) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.categoryMapper = categoryMapper;
    }
    
    @Override
    public com.cho.ecommerce.api.domain.CategoryOptionsOptionVariationsResponseDTO findCategoryOptionsAndVariations(
        Long categoryId) {
        QCategoryEntity category = QCategoryEntity.categoryEntity;
        QOptionEntity option = QOptionEntity.optionEntity;
        QOptionVariationEntity optionVariation = QOptionVariationEntity.optionVariationEntity;
        
        List<Tuple> results = queryFactory
            .select(category.categoryId, category.name,
                option.optionId, option.value,
                optionVariation.optionVariationId, optionVariation.value)
            .from(category)
            .join(category.optionEntities, option)
            .leftJoin(option.optionVariations, optionVariation)
            .where(category.categoryId.eq(categoryId))
            .fetch();
        
        return categoryMapper.mapToCategoryOptionsResponse(results);
    }
}
