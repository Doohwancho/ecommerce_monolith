package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertROLEsStepConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Bean
    public Tasklet createAuthoritiesTasklet() {
        return (contribution, chunkContext) -> {
            if (!authorityRepository.findByAuthority("ROLE_USER").isPresent()) {
                AuthorityEntity userRole = new AuthorityEntity("ROLE_USER");
                authorityRepository.save(userRole);
            }
            if (!authorityRepository.findByAuthority("ROLE_ADMIN").isPresent()) {
                AuthorityEntity adminRole = new AuthorityEntity("ROLE_ADMIN");
                authorityRepository.save(adminRole);
            }
            return RepeatStatus.FINISHED;
        };
    }
    
    
    @Bean
    public Step createAuthoritiesStep() {
        return stepBuilderFactory.get("createAuthoritiesStep")
            .tasklet(createAuthoritiesTasklet())
            .build();
    }
}
