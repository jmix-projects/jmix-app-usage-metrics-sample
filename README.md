**Database**: PostgreSQL <br/>
**Add-ons**: Data Tools, Business Calendars, Quartz.

**Necessary dependencies**

implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
implementation 'org.springframework:spring-jdbc'




**Key classes and services**
[ActuatorSecurityConfiguration](src/main/java/com/company/businessmetrics/ActuatorSecurityConfiguration.java) - the spring security configuration for getting direct access to the prometheus actuator endpoint without the authorization

[LoginEvent](src/main/java/com/company/businessmetrics/entity/LoginEvent.java) - entity for representing event of user entering into the system

[UserLoginListener](src/main/java/com/company/businessmetrics/app/UserLoginListener.java) - the listener for the login events registrating

[ActiveUsersMetricsDAO](src/main/java/com/company/businessmetrics/app/ActiveUsersMetricsDAO.java) - the service for getting data from the database
[ActiveUsersMetricsService](src/main/java/com/company/businessmetrics/app/ActiveUsersMetricsService.java) - the service for the micrometer metrics data modifying 
[BusinessDaysService](src/main/java/com/company/businessmetrics/app/BusinessDaysService.java) - the service for getting buisness days for the week or month

[ActiveUsersMetricsUpdateJob](src/main/java/com/company/businessmetrics/app/ActiveUsersMetricsUpdateJob.java) - the job for the metrics state updating
[MetricsConfiguration](src/main/java/com/company/businessmetrics/app/MetricsConfiguration.java) - the configuration for the job

 **Grafana dashboard**
