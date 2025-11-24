package com.company.businessmetrics.app;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {
    @Value("${businessMetrics.activeUsers.job.cronExpression}")
    private String cronExpression;

    @Bean("appname_ActiveUsersMetricsUpdateJob")
    JobDetail metricsUpdateJob() {
        return JobBuilder.newJob()
                .ofType(ActiveUsersMetricsUpdateJob.class)
                .storeDurably()
                .withIdentity("Active Users Metrics Updating")
                .build();
    }

    @Bean("appname_ActiveUsersMetricsUpdateTrigger")
    Trigger indexingQueueProcessingTrigger(@Qualifier("appname_ActiveUsersMetricsUpdateJob") JobDetail metricsUpdateJob) {
        return TriggerBuilder.newTrigger()
                .withIdentity("Active Users Metrics Updating Cron Trigger")
                .forJob(metricsUpdateJob)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
}
