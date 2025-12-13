package io.github.pmckeown.dependencytrack.metrics;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_METRICS_PROJECT_CURRENT;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.metrics.MetricsBuilder.aMetrics;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.project.ProjectBuilder;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    MetricsMojo metricsMojo;

    @BeforeEach
    @Basedir(TEST_PROJECT)
    @InjectMojo(goal = "metrics")
    void setUp(MetricsMojo mojo) {
        metricsMojo = mojo;
        configureMojo(metricsMojo);
    }

    @Test
    void thatMetricsCanBeRetrievedForCurrentProject() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));

        metricsMojo.setProjectName("testName");
        metricsMojo.setProjectVersion("99.99");

        metricsMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }

    @Test
    void thatWhenMetricsAreNotInProjectTheyAreRetrievedExplicitly() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/noMetrics.json")));
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT))
                .willReturn(aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        metricsMojo.setProjectName("noMetrics");
        metricsMojo.setProjectVersion("1.0.0");
        metricsMojo.setPollingConfig(PollingConfig.disabled());

        metricsMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(1), getRequestedFor(urlPathMatching(V1_METRICS_PROJECT_CURRENT)));
    }

    @Test
    void thatExceptionIsThrownWhenMetricsCannotBeRetrievedForCurrentProject() {
        assertThrows(MojoExecutionException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse().withBodyFile("api/v1/project/noMetrics.json")));
            stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT))
                    .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

            metricsMojo.setProjectName("noMetrics");
            metricsMojo.setProjectVersion("1.0.0");
            metricsMojo.setPollingConfig(new PollingConfig(false, 1, 1));
            metricsMojo.setFailOnError(true);
            metricsMojo.execute();
        });
    }

    @Test
    void thatAnyCriticalIssuesPresentCanFailTheBuild() {
        assertThrows(MojoFailureException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse()
                            .withBody(asJson(ProjectBuilder.aProject()
                                    .withUuid("1234")
                                    .withName("test-project")
                                    .withVersion("1.2.3")
                                    .withMetrics(aMetrics()
                                            .withCritical(101)
                                            .withHigh(201)
                                            .withMedium(301)
                                            .withLow(401)
                                            .withUnassigned(501))
                                    .build()))));

            metricsMojo.setProjectName("test-project");
            metricsMojo.setProjectVersion("1.2.3");
            metricsMojo.setMetricsThresholds(new MetricsThresholds(100, 200, 300, 400, 500));

            metricsMojo.execute();
            fail("MojoFailureException expected");
        });
    }

    @Test
    void thatTheMetricsIsSkippedWhenSkipIsTrue() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        metricsMojo.setSkip("true");
        metricsMojo.setProjectName("testName");
        metricsMojo.setProjectVersion("99.99");

        metricsMojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }
}
