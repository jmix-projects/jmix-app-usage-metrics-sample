<h1>DAU, MAU, WAU metrics demo application</h1>

<h2>Base settings and environment</h2>
**Database**: PostgreSQL <br/>
**Jmix Add-ons**: Data Tools, Business Calendars, Quartz.

<h2>Necessary dependencies</h2>

```
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
implementation 'org.springframework:spring-jdbc'
```

<h2>Application properties</h2>

```
management.endpoints.web.exposure.include=*
management.metrics.tags.application=business-metrics-app
businessMetrics.activeUsers.job.cronExpression=0/10 * * * * ?
```

<h2>Key application classes and services</h2>

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

 <h2>Grafana dashboard. Key concepts</h2>
 
 <h3>The dashboard variables for data filtering</h3>
 
 <img width="974" height="200" alt="image" src="https://github.com/user-attachments/assets/d1f47ec2-5f92-4197-a0c1-583e01e463c9" />

<h3>The PromQL queries for getting metrics data</h3>

```
active_users_dau{application="$application", instance="$instance"}
active_users_wau{application="$application", instance="$instance"}
active_users_mau{application="$application", instance="$instance"}
```
<h3>Source data</h3>
This is the result of following query executing

```
active_users_wau{application="$application", instance="$instance"}
```

<img width="1278" height="393" alt="image" src="https://github.com/user-attachments/assets/dc94cb83-38e3-4dd2-868f-7e0829370cab" />

<h3>The used transformations</h3>

Labels to fields
<img width="1273" height="611" alt="image" src="https://github.com/user-attachments/assets/660a54cb-b13b-4fff-9ed5-b854b468aba2" />

**Format time** transformation using:
<img width="1279" height="697" alt="image" src="https://github.com/user-attachments/assets/b7f81dc4-d354-419f-9068-12969e267bfa" />

Group By
<img width="1281" height="775" alt="image" src="https://github.com/user-attachments/assets/061f77ec-305a-43a9-a8a6-1e0253f37e93" />

Join by field
<img width="1258" height="700" alt="image" src="https://github.com/user-attachments/assets/6978a97a-4c85-4c7b-aef7-27404e6564cc" />

Organize fields by name
<img width="1278" height="763" alt="image" src="https://github.com/user-attachments/assets/bcac6fe2-c3cb-4391-9500-0f0c8c870052" />

Add field from calculation
<img width="1273" height="558" alt="image" src="https://github.com/user-attachments/assets/4ca303c8-5963-49f2-9efa-110ddb8a4899" />

Organize field by name
<img width="1270" height="720" alt="image" src="https://github.com/user-attachments/assets/b8d56e98-2039-44d2-9823-946277ce1795" />

<h3>Vizualizations</h3>

**Table** vizualization
<img width="733" height="349" alt="image" src="https://github.com/user-attachments/assets/4c9c7ea9-e6bf-43b8-a65a-25616f8d9ac6" />

**Bar chart** vizualization
<img width="858" height="341" alt="image" src="https://github.com/user-attachments/assets/26a86a0b-2235-4320-8ee1-be752084e764" />

<h3>Full dashboard configuration</h3>
 
The whole dashboard configuration could be found here [Dashboard configuration](dashboards/buiseness_metrics.json)
