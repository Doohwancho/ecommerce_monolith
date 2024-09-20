package com.cho.ecommerce.global.config.batch.scheduled;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledJobConfig {
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job inactiveUserJob;
    
    
    //매일 새벽 3시에 inactiveUserJob 을 실행한다.
    //(cron = "초 분 시 일 월 요일")
    @Scheduled(cron = "0 0 3 * * 7", zone = "UTC") //매주 일요일 새벽 3시에 해당 Job을 실행한다.
    public void performInactiveUserJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
        jobLauncher.run(inactiveUserJob, params);
    }
}
