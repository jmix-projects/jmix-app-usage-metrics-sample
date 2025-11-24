package com.company.businessmetrics.app;

import com.company.businessmetrics.entity.MetricDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.company.businessmetrics.app.ActiveUsersMetricsService.MetricType.*;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyMap;

@Component
@SuppressWarnings("SqlNoDataSourceInspection")
public class ActiveUsersMetricsDAO {

    private final static String CALENDAR_CODE = "businessCalendarSample";

    private static final String DAU_SQL = """
            select count(DISTINCT user_id)::integer
                from LOGIN_EVENT
                where
                    DATE_TRUNC('day', LOGIN_TIME) = CURRENT_DATE
            """;

    private static final String WAU_SQL = """
            select count(DISTINCT user_id)::integer
                from LOGIN_EVENT
                where
                    DATE_TRUNC('day', LOGIN_TIME) IN (:businessDays)
            """;

    private static final String MAU_SQL = """
            select count(DISTINCT user_id)::integer
                from LOGIN_EVENT
                where
                    DATE_TRUNC('day', LOGIN_TIME) IN (:businessDays)
            """;

    private static final String AVG_DAU_BY_WAU_SQL = """
            select sum(count_per_day)/:businessDaysCount
                from (select count(DISTINCT user_id) as count_per_day
                        from LOGIN_EVENT
                        where
                            DATE_TRUNC('day', LOGIN_TIME) IN (:businessDays)
                        group by DATE_TRUNC('day', LOGIN_TIME))
            """;

    private static final String AVG_DAU_BY_MAU_SQL = """
            select sum(count_per_day)/:businessDaysCount
                from (select count(DISTINCT user_id) as count_per_day
                        from LOGIN_EVENT
                        where
                            DATE_TRUNC('day', LOGIN_TIME) IN (:businessDays)
                        group by DATE_TRUNC('day', LOGIN_TIME))
            """;

    private final BusinessDaysService businessDaysService;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ActiveUsersMetricsDAO(BusinessDaysService businessDaysService, DataSource dataSource) {
        this.businessDaysService = businessDaysService;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<ActiveUsersMetricsService.MetricType, MetricDTO> loadMetrics() {
        return Map.of(
                DAU, getDauDTO(),
                WAU, getWauDTO(),
                MAU, getMauDTO()
        );
    }

    private MetricDTO getDauDTO() {
        Integer dau = jdbcTemplate.queryForObject(DAU_SQL, emptyMap(),Integer.class);
        return new MetricDTO(dau, null, null);
    }

    private MetricDTO getWauDTO() {
        List<LocalDate> businessDaysOfWeek = businessDaysService.getWorkingDaysOfWeek(CALENDAR_CODE, now());
        Float avgDauForWeek = getAvgDauForWeek(businessDaysOfWeek);
        Integer wau = getWau(businessDaysOfWeek);
        return new MetricDTO(wau, avgDauForWeek, businessDaysOfWeek.size());
    }

    private MetricDTO getMauDTO() {
        List<LocalDate> businessDaysOfMonth = businessDaysService.getWorkingDaysOfMonth(CALENDAR_CODE, now());
        Float avgDauForMonth = getAvgDauForMonth(businessDaysOfMonth);
        Integer mau = getMau(businessDaysOfMonth);
        return new MetricDTO(mau, avgDauForMonth, businessDaysOfMonth.size());
    }

    private Integer getWau(List<LocalDate> businessDaysOfWeek) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("businessDays", businessDaysOfWeek);
        return jdbcTemplate.queryForObject(WAU_SQL, parameters, Integer.class);
    }

    private Float getAvgDauForWeek(List<LocalDate> businessDaysOfWeek) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("businessDaysCount", businessDaysOfWeek.size());
        parameters.addValue("businessDays", businessDaysOfWeek);
        return jdbcTemplate.queryForObject(AVG_DAU_BY_WAU_SQL, parameters, Float.class);
    }

    private Integer getMau(List<LocalDate> businessDaysOfMonth) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("businessDays", businessDaysOfMonth);
        return jdbcTemplate.queryForObject(MAU_SQL, parameters, Integer.class);
    }

    private Float getAvgDauForMonth(List<LocalDate> businessDaysOfMonth) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("businessDaysCount", businessDaysOfMonth.size());
        parameters.addValue("businessDays", businessDaysOfMonth);
        return jdbcTemplate.queryForObject(AVG_DAU_BY_MAU_SQL, parameters, Float.class);
    }
}
