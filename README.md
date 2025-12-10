**Database**: PostgreSQL <br/>
**Add-ons**: Data Tools, Business Calendars, Quartz.

**Necessary dependencies**

```
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
implementation 'org.springframework:spring-jdbc'
```

**Application properties**
```
management.endpoints.web.exposure.include=*
management.metrics.tags.application=business-metrics-app
businessMetrics.activeUsers.job.cronExpression=0/10 * * * * ?
```

**Key classes and services**

[ActuatorSecurityConfiguration](src/main/java/com/company/businessmetrics/ActuatorSecurityConfiguration.java) - the spring security configuration for getting direct access to the prometheus actuator endpoint without the authorization


[LoginEvent](src/main/java/com/company/businessmetrics/entity/LoginEvent.java) - the entity for representing event of user entering into the system
[MetricDTO](src/main/java/com/company/businessmetrics/entity/MetricDTO.java) - the data transfer object for getting data from the database

[UserLoginListener](src/main/java/com/company/businessmetrics/app/UserLoginListener.java) - the listener for the login events registrating


[ActiveUsersMetricsDAO](src/main/java/com/company/businessmetrics/app/ActiveUsersMetricsDAO.java) - the service for getting data from the database

_Note_. The DTO and JDBC are used instead of using JPA technology because of the specific complex SQL quries. For example:
```
select sum(count_per_day)/:businessDaysCount
    from (select count(DISTINCT user_id) as count_per_day
            from LOGIN_EVENT
            where
                DATE_TRUNC('day', LOGIN_TIME) IN (:businessDays)
            group by DATE_TRUNC('day', LOGIN_TIME))
```
[ActiveUsersMetricsService](src/main/java/com/company/businessmetrics/app/ActiveUsersMetricsService.java) - the service for the micrometer metrics data modifying 

_Note_. The **MultiGaude** type of micrometer metrics is used.

[BusinessDaysService](src/main/java/com/company/businessmetrics/app/BusinessDaysService.java) - the service for getting buisness days for the week or month

_Note_. If you don't have an ability to use Business calendars Jmix Add-on, you can implement your own implmentation of this service.


[ActiveUsersMetricsUpdateJob](src/main/java/com/company/businessmetrics/app/ActiveUsersMetricsUpdateJob.java) - the job for the metrics state updating

[MetricsConfiguration](src/main/java/com/company/businessmetrics/app/MetricsConfiguration.java) - the configuration for the job

_Note_. **businessMetrics.activeUsers.job.cronExpression** application parameter is used to determine the schedule of metrics update.

 **Grafana dashboard**
 The key concepts:
 The dashboard variables for data filtering:
 <img width="974" height="200" alt="image" src="https://github.com/user-attachments/assets/d1f47ec2-5f92-4197-a0c1-583e01e463c9" />
**Format time** transformation using:
<img width="974" height="419" alt="image" src="https://github.com/user-attachments/assets/faa70302-ab8b-416f-b5c2-cf79eac9de52" />

Group By
<img width="974" height="516" alt="image" src="https://github.com/user-attachments/assets/2c981d21-130d-457a-bfd3-f75c8550101f" />


 
 [Dashboard configuration](dashboards/buiseness_metrics.json)
