package com.company.businessmetrics.app;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class ActiveUsersMetricsUpdateJob implements Job {

    @Autowired
    ActiveUsersMetricsService activeUsersMetricsService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        activeUsersMetricsService.updateMetrics();
    }
}