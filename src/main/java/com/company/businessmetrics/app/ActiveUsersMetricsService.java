package com.company.businessmetrics.app;

import com.company.businessmetrics.entity.MetricDTO;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.company.businessmetrics.app.ActiveUsersMetricsService.MetricSubtype.*;
import static com.company.businessmetrics.app.ActiveUsersMetricsService.MetricType.*;

@Component
public class ActiveUsersMetricsService {
    private final MultiGauge dauMetric;
    private final MultiGauge wauMetric;
    private final MultiGauge mauMetric;
    private final ActiveUsersMetricsDAO activeUsersMetricsDAO;

    public ActiveUsersMetricsService(ActiveUsersMetricsDAO activeUsersMetricsDAO, MeterRegistry registry) {
        this.activeUsersMetricsDAO = activeUsersMetricsDAO;
        dauMetric = MultiGauge.builder("active_users_dau")
                .description("The daily active users metric")
                .register(registry);
        wauMetric = MultiGauge.builder("active_users_wau")
                .description("The weekly active users metrics")
                .register(registry);
        mauMetric = MultiGauge.builder("active_users_mau")
                .description("The monthly active users metrics")
                .register(registry);
    }

    public void updateMetrics() {

        Map<MetricType, MetricDTO> loadedMeticValuesMap = activeUsersMetricsDAO.loadMetrics();
        registerMetric(dauMetric, createRows(DAU, loadedMeticValuesMap));
        registerMetric(wauMetric, createRows(WAU, loadedMeticValuesMap));
        registerMetric(mauMetric, createRows(MAU, loadedMeticValuesMap));
    }

    private void registerMetric(MultiGauge metric, List<MultiGauge.Row<?>> metricRow) {
        metric.register(metricRow, true);
    }

    private List<MultiGauge.Row<?>> createRows(MetricType metricType, Map<MetricType, MetricDTO> metricsMap) {
        MetricDTO metric = metricsMap.get(metricType);

        List<MultiGauge.Row<?>> metricRows;
        if (!metricType.hasSubtypes) {
            metricRows = createMetricWithoutSubtypes(metric);
        } else {
            metricRows = createMetricWithSubtypes(metric);
        }
        return metricRows;
    }

    private List<MultiGauge.Row<?>> createMetricWithSubtypes(MetricDTO metric) {
        return List.of(
                createRowWithSubtype(MAIN, metric),
                createRowWithSubtype(AVG_DAU, metric),
                createRowWithSubtype(DAYS_COUNT, metric)
        );
    }

    private MultiGauge.Row<?> createRowWithSubtype(MetricSubtype subtype, MetricDTO metric) {
        return MultiGauge.Row.of(
                Tags.of("subtype", subtype.tagValue),
                subtype.valueExtractor.apply(metric)
        );
    }

    private List<MultiGauge.Row<?>> createMetricWithoutSubtypes(MetricDTO metric) {
        MultiGauge.Row<Number> row = MultiGauge.Row.of(
                Tags.empty(),
                metric.value()
        );
        return List.of(row);
    }


    public enum MetricType {
        DAU(false),
        WAU(true),
        MAU(true);

        private final boolean hasSubtypes;

        MetricType(boolean hasSubtypes) {
            this.hasSubtypes = hasSubtypes;
        }
    }

    enum MetricSubtype {
        MAIN("main_value", MetricDTO::value),
        AVG_DAU("avg_dau", MetricDTO::avgDau),
        DAYS_COUNT("days_count", MetricDTO::businessDays);

        private final String tagValue;
        private final Function<MetricDTO, Number> valueExtractor;

        MetricSubtype(String tagValue, Function<MetricDTO, Number> valueExtractor) {
            this.tagValue = tagValue;
            this.valueExtractor = valueExtractor;
        }
    }
}