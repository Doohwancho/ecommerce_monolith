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
public class InactiveUserJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    private JobDurationListenerConfig jobDurationListenerConfig;
    
    @Bean
    public Job inactiveUserJob(Step userToInactiveMemberStep) {
        return jobBuilderFactory.get("userToInactiveUserJob")
            .incrementer(new RunIdIncrementer())
            .listener(jobDurationListenerConfig)
            .start(userToInactiveMemberStep)
            .build();
    }
}
