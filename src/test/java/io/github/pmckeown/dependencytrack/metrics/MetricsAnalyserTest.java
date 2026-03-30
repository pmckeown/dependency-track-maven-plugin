package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;
import static io.github.pmckeown.dependencytrack.Constants.UNASSIGNED;
import static io.github.pmckeown.dependencytrack.metrics.MetricsAnalyser.ERROR_TEMPLATE;
import static io.github.pmckeown.dependencytrack.metrics.MetricsBuilder.aMetrics;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.test.logging.SpyLoggerRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class MetricsAnalyserTest {

    @InjectMocks
    private MetricsAnalyser metricsAnalyser;

    @Test
    void thatIfCriticalIssuesExistThenAnErrorIsReturned() {
        Metrics metrics = aMetrics().withCritical(100).build();

        assertFalse(metricsAnalyser.analyse(metrics, new MetricsThresholds(0, null, null, null, null)));

        Logger logger = SpyLoggerRegistry.expectLogger(MetricsAnalyser.class);
        verify(logger).warn(ERROR_TEMPLATE, CRITICAL, 100, 0);
    }

    @Test
    void thatIfHighIssuesExistThenAnErrorIsReturned() {
        Metrics metrics = aMetrics().withHigh(200).build();

        assertFalse(metricsAnalyser.analyse(metrics, new MetricsThresholds(null, 0, null, null, null)));

        Logger logger = SpyLoggerRegistry.expectLogger(MetricsAnalyser.class);
        verify(logger).warn(ERROR_TEMPLATE, HIGH, 200, 0);
    }

    @Test
    void thatIfMediumIssuesExistThenAnErrorIsReturned() {
        Metrics metrics = aMetrics().withMedium(300).build();

        assertFalse(metricsAnalyser.analyse(metrics, new MetricsThresholds(null, null, 0, null, null)));

        Logger logger = SpyLoggerRegistry.expectLogger(MetricsAnalyser.class);
        verify(logger).warn(ERROR_TEMPLATE, MEDIUM, 300, 0);
    }

    @Test
    void thatIfLowIssuesExistThenAnErrorIsReturned() {
        Metrics metrics = aMetrics().withLow(400).build();

        assertFalse(metricsAnalyser.analyse(metrics, new MetricsThresholds(null, null, null, 0, null)));

        Logger logger = SpyLoggerRegistry.expectLogger(MetricsAnalyser.class);
        verify(logger).warn(ERROR_TEMPLATE, LOW, 400, 0);
    }

    @Test
    void thatIfUnassignedIssuesExistThenAnErrorIsReturned() {
        Metrics metrics = aMetrics().withUnassigned(500).build();

        assertFalse(metricsAnalyser.analyse(metrics, new MetricsThresholds(null, null, null, null, 0)));

        Logger logger = SpyLoggerRegistry.expectLogger(MetricsAnalyser.class);
        verify(logger).warn(ERROR_TEMPLATE, UNASSIGNED, 500, 0);
    }

    @Test
    void thatIfIssuesExistInMultipleCategoriesThenAllAreLogged() {
        Metrics metrics = aMetrics()
                .withCritical(100)
                .withHigh(200)
                .withMedium(300)
                .withLow(400)
                .withUnassigned(500)
                .build();

        assertFalse(metricsAnalyser.analyse(metrics, new MetricsThresholds(0, 0, 0, 0, 0)));

        Logger logger = SpyLoggerRegistry.expectLogger(MetricsAnalyser.class);
        verify(logger).warn(ERROR_TEMPLATE, CRITICAL, 100, 0);
        verify(logger).warn(ERROR_TEMPLATE, HIGH, 200, 0);
        verify(logger).warn(ERROR_TEMPLATE, MEDIUM, 300, 0);
        verify(logger).warn(ERROR_TEMPLATE, LOW, 400, 0);
        verify(logger).warn(ERROR_TEMPLATE, UNASSIGNED, 500, 0);
    }
}
