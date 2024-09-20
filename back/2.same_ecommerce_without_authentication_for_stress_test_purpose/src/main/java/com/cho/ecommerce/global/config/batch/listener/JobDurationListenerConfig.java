package com.cho.ecommerce.global.config.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobDurationListenerConfig implements JobExecutionListener {
    
    private final Logger log = LoggerFactory.getLogger(JobDurationListenerConfig.class);
    
    private long startTime;
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job started: " + jobExecution.getJobInstance().getJobName());
        startTime = System.currentTimeMillis();
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("Job ended: " + jobExecution.getJobInstance().getJobName() + " with status: "
            + jobExecution.getStatus());
        log.info("Job " + jobExecution.getJobInstance().getJobName()
            + " completed in " + duration + " ms");
    }
}
