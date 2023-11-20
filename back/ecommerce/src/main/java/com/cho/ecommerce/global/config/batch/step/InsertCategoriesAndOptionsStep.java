package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import net.datafaker.Faker;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertCategoriesAndOptionsStep {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OptionRepository optionRepository;
    
    @Autowired
    private OptionVariationRepository optionVariationRepository;
    
    private final Faker faker = new Faker();
    
    public String sizeTrimmer(String str, int size){
        int len = str.length();
        if(len >= size) {
            return str.substring(len-size, len-1);
        }
        return str;
    }
    
    @Bean
    public Tasklet createCategoriesAndOptionsTasklet() {
        return (contribution, chunkContext) -> {
            List<CategoryEntity> lists = new ArrayList<>();
    
            // Generate categories
            for (int i = 0; i < 5; i++) { //TODO - error: 이상하게 카테고리만 x2로 넣어진다. (ex. 5개 넣으면 10개 들어감)
                CategoryEntity category = new CategoryEntity();
                String categoryCode = sizeTrimmer(faker.code().asin(), DatabaseConstants.CATEGORY_CODE_SIZE);
                String categoryName = sizeTrimmer(faker.commerce().department(), DatabaseConstants.CATEGORY_NAME_SIZE);
        
                category.setCategoryCode(categoryCode);
                category.setName(categoryName);
        
                // Generate options for each category
                Set<OptionEntity> options = new HashSet<>();
                for (int j = 0; j < 3; j++) {
                    OptionEntity option = new OptionEntity();
                    String optionValue = sizeTrimmer(faker.commerce().material(), DatabaseConstants.OPTION_VALUE_SIZE);
            
                    option.setValue(optionValue);
                    option.setCategory(category);
                    option.setOptionVariations(new ArrayList<>());
                    optionRepository.save(option);
                    options.add(option);
            
                    // Generate option variations for each option
                    for (int k = 0; k < 3; k++) {
                        OptionVariationEntity optionVariation = new OptionVariationEntity();
                
                        String optionVariationValue = sizeTrimmer(faker.color().name(), DatabaseConstants.OPTION_VARIATION_VALUE_SIZE);
                
                        optionVariation.setValue(optionVariationValue);
                        optionVariation.setOption(option);
                
                        optionVariationRepository.save(optionVariation);
                
                        option.getOptionVariations().add(optionVariation);
                    }
                }
                category.setOptionEntities(options);
                lists.add(category);
            }
            categoryRepository.saveAllAndFlush(lists);
            return RepeatStatus.FINISHED;
        };
    }
    
    @Bean
    public Step createCategoriesAndOptionsStep() {
        return stepBuilderFactory.get("createCategoriesAndOptionsStep")
            .tasklet(createCategoriesAndOptionsTasklet())
            .build();
    }
}
