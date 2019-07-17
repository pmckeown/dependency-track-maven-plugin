package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class MetricsActionTest {

    private static final int INHERITED_RISK_SCORE = 1;

    @InjectMocks
    private MetricsAction metricsAction;

    @Mock
    private MetricsClient metricsClient;

    @Mock
    private Logger logger;

    @Test
    public void thatMetricsCanBeRetrieved() throws Exception {
        Response<Metrics> response = new Response<>(200, "OK", true, anOptionalMetrics());
        doReturn(response).when(metricsClient).getMetrics(any(Project.class));

        Metrics metrics = metricsAction.getMetrics(aProject());

        assertThat(metrics, is(not(nullValue())));
        assertThat(metrics.getInheritedRiskScore(), is(equalTo(INHERITED_RISK_SCORE)));
    }

    @Test
    public void thatAnExceptionOccursWhenNoMetricsCanBeFound() {
        Response<Metrics> response = new Response<>(200, "Not Found", false, Optional.empty());
        doReturn(response).when(metricsClient).getMetrics(any(Project.class));

        try {
            metricsAction.getMetrics(aProject());
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatAnExceptionOccursResultsInAnException() {
        doThrow(UnirestException.class).when(metricsClient).getMetrics(any(Project.class));

        try {
            metricsAction.getMetrics(aProject());
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    private Project aProject() {
        return new Project("123", "p", "1", null);
    }

    private Optional<Metrics> anOptionalMetrics() {
        return Optional.of(new Metrics(1));
    }
}
