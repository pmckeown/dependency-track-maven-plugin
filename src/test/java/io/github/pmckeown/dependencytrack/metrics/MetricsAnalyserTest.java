package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;
import static io.github.pmckeown.dependencytrack.metrics.MetricsBuilder.aMetrics;
import static io.github.pmckeown.dependencytrack.metrics.MetricsAnalyser.ERROR_TEMPLATE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MetricsAnalyserTest {

    @InjectMocks
    private MetricsAnalyser metricsAnalyser;

    @Mock
    private Logger logger;

    @Test
    public void thatIfCriticalIssuesExistThenAnErrorIsReturned() throws Exception {
        Metrics metrics = aMetrics().withCritical(100).build();

        try {
            metricsAnalyser.analyse(metrics, new MetricsThresholds(0, null, null, null));
            fail("MojoFailureException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoFailureException.class));
        }

        verify(logger).warn(ERROR_TEMPLATE, CRITICAL, 100, 0);
    }

    @Test
    public void thatIfHighIssuesExistThenAnErrorIsReturned() throws Exception {
        Metrics metrics = aMetrics().withHigh(200).build();

        try {
            metricsAnalyser.analyse(metrics, new MetricsThresholds(null, 0, null, null));
            fail("MojoFailureException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoFailureException.class));
        }

        verify(logger).warn(ERROR_TEMPLATE, HIGH, 200, 0);
    }

    @Test
    public void thatIfMediumIssuesExistThenAnErrorIsReturned() throws Exception {
        Metrics metrics = aMetrics().withMedium(300).build();

        try {
            metricsAnalyser.analyse(metrics, new MetricsThresholds(null, null, 0, null));
            fail("MojoFailureException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoFailureException.class));
        }

        verify(logger).warn(ERROR_TEMPLATE, MEDIUM, 300, 0);
    }

    @Test
    public void thatIfLowIssuesExistThenAnErrorIsReturned() throws Exception {
        Metrics metrics = aMetrics().withLow(400).build();

        try {
            metricsAnalyser.analyse(metrics, new MetricsThresholds(null, null, null, 0));
            fail("MojoFailureException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoFailureException.class));
        }

        verify(logger).warn(ERROR_TEMPLATE, LOW, 400, 0);
    }

    @Test
    public void thatIfIssuesExistInMultipleCategoriesThenAllAreLogged() throws Exception {
        Metrics metrics = aMetrics()
                .withCritical(100)
                .withHigh(200)
                .withMedium(300)
                .withLow(400).build();

        try {
            metricsAnalyser.analyse(metrics, new MetricsThresholds(0, 0, 0, 0));
            fail("MojoFailureException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoFailureException.class));
        }

        verify(logger).warn(ERROR_TEMPLATE, CRITICAL, 100, 0);
        verify(logger).warn(ERROR_TEMPLATE, HIGH, 200, 0);
        verify(logger).warn(ERROR_TEMPLATE, MEDIUM, 300, 0);
        verify(logger).warn(ERROR_TEMPLATE, LOW, 400, 0);
    }
}
