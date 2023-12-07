package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionVariationEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class OptionRepositoryImpl implements OptionRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public OptionRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<OptionEntity> findOptionsByCategory_CategoryId(Long categoryId) {
        
        QOptionEntity option = QOptionEntity.optionEntity;
        return queryFactory.selectFrom(option)
            .where(option.category.categoryId.eq(categoryId))
            .fetch();
    }
    
    @Override
    public List<com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO> findOptionsAndOptionVariationsByCategoryId(Long categoryId){
        QOptionEntity option = QOptionEntity.optionEntity;
        QOptionVariationEntity optionVariation = QOptionVariationEntity.optionVariationEntity;
        
        List<Tuple> results = queryFactory
            .select(option, optionVariation)
            .from(option)
            .innerJoin(option.optionVariations, optionVariation)
            .where(option.category.categoryId.eq(categoryId))
            .fetch();
    
        List<com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO> responseList = new ArrayList<>();
        for (Tuple tuple : results) {
            com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO dto = new com.cho.ecommerce.api.domain.OptionsOptionVaraitonsResponseDTO();
            dto.setCategoryId(categoryId);
            dto.setOptionId(tuple.get(option.optionId));
            dto.setOptionName(tuple.get(option.value));
            dto.setOptionVariationName(tuple.get(optionVariation.value));
            responseList.add(dto);
        }
        return responseList;
    }
}

