package com.cho.ecommerce.global.config.batch.step;

import java.util.Arrays;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SecondStepConfig {
    
    @Bean
    public ItemReader<String> secondReader() {
        return new ListItemReader<>(Arrays.asList("four", "five", "six"));
    }
    
    @Bean
    public ItemProcessor<String, String> secondProcessor() {
        return item -> item.toUpperCase();
    }
    
    @Bean
    public ItemWriter<String> secondWriter() {
        return items -> items.forEach(System.out::println);
    }
    
    @Bean
    public Step secondStep1(StepBuilderFactory stepBuilderFactory, ItemReader<String> secondReader,
        ItemProcessor<String, String> secondProcessor, ItemWriter<String> secondWriter) {
        return stepBuilderFactory.get("secondStep1")
            .<String, String>chunk(2)
            .reader(secondReader)
            .processor(secondProcessor)
            .writer(secondWriter)
            .build();
    }

}

