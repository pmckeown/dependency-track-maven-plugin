package io.github.pmckeown.dependencytrack.metrics;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.TestMojoLoader.loadMetricsMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_METRICS_PROJECT_CURRENT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MetricsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    @Test
    public void thatMetricsCanBeRetrievedForCurrentProject() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        MetricsMojo metricsMojo = loadMetricsMojo(mojoRule);
        metricsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        metricsMojo.setApiKey("abc123");
        metricsMojo.setProjectName("testName");
        metricsMojo.setProjectVersion("99.99");

        metricsMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(ResourceConstants.V1_PROJECT)));
    }

    @Test
    public void thatMetricsAreNotInProjectTheyAreRetrievedExplicitly() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        MetricsMojo metricsMojo = loadMetricsMojo(mojoRule);
        metricsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        metricsMojo.setApiKey("abc123");
        metricsMojo.setProjectName("noMetrics");
        metricsMojo.setProjectVersion("1.0.0");

        metricsMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
        verify(exactly(1), getRequestedFor(urlPathMatching(V1_METRICS_PROJECT_CURRENT)));
    }

    @Test
    public void thatExceptionIsThrownWhenMetricsCannotBeRetrievedForCurrentProject() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        MetricsMojo metricsMojo = loadMetricsMojo(mojoRule);
        metricsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        metricsMojo.setApiKey("abc123");
        metricsMojo.setProjectName("noMetrics");
        metricsMojo.setProjectVersion("1.0.0");

        try {
            metricsMojo.execute();
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }
}
