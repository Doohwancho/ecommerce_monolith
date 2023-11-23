package com.cho.ecommerce.global.config.batch.job;

import com.cho.ecommerce.global.config.batch.listener.JobDurationListenerConfig;
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
    private JobDurationListenerConfig jobDurationListenerConfig;
 
    @Bean
    public Job dataInitializationJob(Step createAuthoritiesStep
        , Step createAdminStep
        , Step createTestUserStep
        , Step generateFakeUserStep
        , Step generateFakeProductStep
        , Step generateFakeOrderStep
        , Step userToInactiveMemberStep
    ) {
        return jobBuilderFactory.get("dataInitializationJob")
            .incrementer(new RunIdIncrementer())
            .listener(jobDurationListenerConfig)
            .start(createAuthoritiesStep)
            .next(createAdminStep)
            .next(createTestUserStep)
            .next(generateFakeUserStep)
            .next(generateFakeProductStep)
            .next(generateFakeOrderStep)
            .next(userToInactiveMemberStep)
            .build();
    }
}