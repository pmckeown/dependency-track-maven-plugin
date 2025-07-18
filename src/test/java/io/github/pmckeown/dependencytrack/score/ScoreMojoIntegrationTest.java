package io.github.pmckeown.dependencytrack.score;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.TestMojoLoader.loadScoreMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_METRICS_PROJECT_CURRENT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.PollingConfig;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

public class ScoreMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    private ScoreMojo scoreMojo;

    @Before
    public void setup() throws Exception {
        scoreMojo = loadScoreMojo(mojoRule);
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        scoreMojo.setProjectName("dependency-track");
        scoreMojo.setProjectVersion("3.6.0-SNAPSHOT");
    }

    @Test
    public void thatAllProjectsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }

    @Test
    public void thatARiskScoreHigherThanTheThresholdCausesBuildToFailEvenWithFailOnErrorFalse() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(1);
        scoreMojo.setFailOnError(false);

        try {
            scoreMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatARiskScoreEqualToTheThresholdDoesNothing() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(3);

        try {
            scoreMojo.execute();
        } catch (MojoFailureException ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatFailureToGetARiskScoreEqualThrowsAnException() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(3);

        try {
            scoreMojo.execute();
        } catch (MojoFailureException ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatARiskScoreLowerThanTheThresholdDoesNothing() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(999);

        try {
            scoreMojo.execute();
        } catch (MojoFailureException ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatWhenNoMetricsHaveBeenCalculatedThenTheMetricsAreRetrieved() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/noMetrics.json")));
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT))
                .willReturn(aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        scoreMojo.setProjectName("noMetrics");
        scoreMojo.setProjectVersion("1.0.0");
        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_METRICS_PROJECT_CURRENT)));
    }

    @Test
    public void thatWhenNoMetricsHaveBeenTheTheCallIsRetriedTheCorrectNumberOfTimes() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/noMetrics.json")));
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT))
                .willReturn(aResponse().withStatus(404).withBody("The project could not be found.")));

        scoreMojo.setProjectName("noMetrics");
        scoreMojo.setProjectVersion("1.0.0");
        scoreMojo.setFailOnError(true);
        scoreMojo.setPollingConfig(new PollingConfig(true, 1, 1));

        try {
            scoreMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_METRICS_PROJECT_CURRENT)));
    }

    @Test
    public void thatWhenFailOnErrorIsFalseAFailureFromToDependencyTrackDoesNotFailTheBuild() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(notFound()));

        try {
            scoreMojo.setFailOnError(false);
            scoreMojo.execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }

    @Test
    public void thatWhenFailOnErrorIsTrueAFailureFromToDependencyTrackDoesFailTheBuild() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(notFound()));

        try {
            scoreMojo.setFailOnError(true);
            scoreMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatWhenFailOnErrorIsFalseAFailureToConnectToDependencyTrackDoesNotFailTheBuild() {
        // No Wiremock Stubbing

        try {
            scoreMojo.setDependencyTrackBaseUrl("http://localghost:80");
            scoreMojo.setFailOnError(false);
            scoreMojo.execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatWhenFailOnErrorIsTrueAFailureToConnectToDependencyTrackDoesFailTheBuild() throws Exception {
        // No Wiremock Stubbing

        try {
            scoreMojo.setDependencyTrackBaseUrl("http://localghost:80");
            scoreMojo.setFailOnError(true);
            scoreMojo.execute();
            fail("No exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatTheScoreIsSkippedWhenSkipIsTrue() throws Exception {
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        scoreMojo.setProjectName("dependency-track");
        scoreMojo.setProjectVersion("3.6.0-SNAPSHOT");
        scoreMojo.setSkip("true");

        scoreMojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }
}
