package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static io.github.pmckeown.TestMojoLoader.loadMetricsMojo;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.API_V1_METRICS_PROJECT_CURRENT;

public class MetricsMojoTest extends AbstractDependencyTrackMojoTest {

    @Test
    public void thatMetricsMojoCanLog() throws Exception {
        stubFor(get(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        MetricsMojo metricsMojo = loadMetricsMojo(mojoRule);
        metricsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        metricsMojo.setApiKey("abc123");

        metricsMojo.execute();
    }
}
