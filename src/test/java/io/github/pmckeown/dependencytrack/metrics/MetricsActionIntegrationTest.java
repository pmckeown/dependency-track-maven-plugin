package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.score.Project;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.API_V1_METRICS_PROJECT_CURRENT;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MetricsActionIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    private MetricsClient metricsClient = new MetricsClient();

    private Logger logger = new Logger(new SilentLog());

    @Test
    public void thatMetricsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        Response<Metrics> metrics = metricsClient.getMetrics(getCommonConfig(), logger,
                new Project("123", PROJECT_NAME, PROJECT_VERSION, null));

        assertThat(metrics, is(not(nullValue())));
        verify(exactly(1), getRequestedFor(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)));
    }
}
