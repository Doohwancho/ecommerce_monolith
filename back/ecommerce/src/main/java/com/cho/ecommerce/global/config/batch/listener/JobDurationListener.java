package com.cho.ecommerce.global.config.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobDurationListener implements JobExecutionListener {
    private final Logger log = LoggerFactory.getLogger(JobDurationListener.class);
    
    private long startTime;
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.warn("Job started: " + jobExecution.getJobInstance().getJobName());
        startTime = System.currentTimeMillis();
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
    
        log.warn("Job ended: " + jobExecution.getJobInstance().getJobName() + " with status: " + jobExecution.getStatus());
        log.warn("Job " + jobExecution.getJobInstance().getJobName()
            + " completed in " + duration + " ms");
    }
}
