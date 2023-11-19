package com.cho.ecommerce.global.config.batch.job;

import com.cho.ecommerce.global.config.batch.listener.JobDurationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializationJobConfig {
    
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    private JobDurationListener jobDurationListener;
 
    @Bean
    public Job dataInitializationJob(Step createAuthoritiesStep, Step createAdminStep, Step createTestUserStep, Step insertUserWithROLE_USERStep) {
        return jobBuilderFactory.get("data-initialization-job")
            .incrementer(new RunIdIncrementer())
            .listener(jobDurationListener)
            .start(createAuthoritiesStep)
            .next(createAdminStep)
            .next(createTestUserStep)
            .next(insertUserWithROLE_USERStep)
            .build();
    }

}