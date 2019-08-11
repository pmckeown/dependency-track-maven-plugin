package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Poller;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.builders.MetricsBuilder;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static io.github.pmckeown.dependencytrack.builders.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.builders.ResponseBuilder.aNotFoundResponse;
import static io.github.pmckeown.dependencytrack.builders.ResponseBuilder.aSuccessResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MetricsActionTest {

    private static final int INHERITED_RISK_SCORE = 1;

    @InjectMocks
    private MetricsAction metricsAction;

    @Mock
    private MetricsClient metricsClient;

    @Mock
    private CommonConfig commonConfig;

    @Spy
    private Poller<Metrics> poller = new Poller<>();

    @Mock
    private Logger logger;

    @Test
    public void thatMetricsCanBeRetrieved() throws Exception {
        doReturn(aSuccessResponse().withBody(aMetrics()).build()).when(metricsClient).getMetrics(any(Project.class));
        doReturn(PollingConfig.defaults()).when(commonConfig).getPollingConfig();

        Metrics metrics = metricsAction.getMetrics(aProject().build());

        assertThat(metrics, is(not(nullValue())));
        assertThat(metrics.getInheritedRiskScore(), is(equalTo(INHERITED_RISK_SCORE)));
    }

    @Test
    public void thatAnExceptionOccursWhenNoMetricsCanBeFound() {
        doReturn(aSuccessResponse().build()).when(metricsClient).getMetrics(any(Project.class));
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            metricsAction.getMetrics(aProject().build());
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatAnExceptionOccurringResultsInAnException() {
        doThrow(UnirestException.class).when(metricsClient).getMetrics(any(Project.class));
        doReturn(PollingConfig.defaults()).when(commonConfig).getPollingConfig();

        try {
            metricsAction.getMetrics(aProject().build());
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatRefreshMetricsCanBeCalled() {
        doReturn(aSuccessResponse().build()).when(metricsClient).refreshMetrics(any(Project.class));
        try {
            metricsAction.refreshMetrics(aProject().build());
        } catch (Exception e) {
            fail("No exception expected");
        }
        verify(logger).debug(anyString());
    }

    @Test
    public void thatRefreshMetricsDoesNotErrorWhenProjectNotFound() {
        doReturn(aNotFoundResponse().build()).when(metricsClient).refreshMetrics(any(Project.class));
        try {
            metricsAction.refreshMetrics(aProject().build());
        } catch (Exception e) {
            fail("No exception expected");
        }
        verify(logger).debug(anyString(), anyString());
    }

    @Test
    public void thatNoExceptionsAreThrownIfRefreshMetricsErrors() {
        doThrow(new UnirestException("Boom")).when(metricsClient).refreshMetrics(any(Project.class));

        try {
            metricsAction.refreshMetrics(aProject().build());
        } catch (Exception e) {
            fail("No exception expected");
        }
        verify(logger).error(anyString(), anyString());
    }

    private Metrics aMetrics() {
        return MetricsBuilder.aMetrics()
                .withInheritedRiskScore(INHERITED_RISK_SCORE)
                .withCritical(100)
                .withHigh(200)
                .withMedium(300)
                .withLow(400)
                .withUnassigned(500)
                .withVulnerabilities(600)
                .withComponents(700)
                .withFindingsTotal(800)
                .withFindingsAudited(900)
                .withFirstOccurrence(Long.MIN_VALUE)
                .withLastOccurrence(Long.MAX_VALUE)
                .build();
    }
}
