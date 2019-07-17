package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static io.github.pmckeown.TestMojoLoader.loadMetricsMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;

@Ignore
public class MetricsMojoTest extends AbstractDependencyTrackMojoTest {

    @Test
    public void thatMetricsMojoCanLog() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        MetricsMojo metricsMojo = loadMetricsMojo(mojoRule);
        metricsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        metricsMojo.setProjectName("testName");
        metricsMojo.setProjectVersion("99.99");
        metricsMojo.setApiKey("abc123");

        metricsMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(ResourceConstants.V1_PROJECT)));
    }
}
