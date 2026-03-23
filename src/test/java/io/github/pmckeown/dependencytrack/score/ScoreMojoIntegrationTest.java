package io.github.pmckeown.dependencytrack.score;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_METRICS_PROJECT_CURRENT;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.PollingConfig;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;

class ScoreMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    @Test
    @InjectMojo(goal = "score")
    void thatAllProjectsCanBeRetrieved(ScoreMojo scoreMojo) throws Exception {
        configureMojo(scoreMojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }

    @Test
    @InjectMojo(goal = "score")
    void thatARiskScoreHigherThanTheThresholdCausesBuildToFailEvenWithFailOnErrorFalse(ScoreMojo scoreMojo)
            throws Exception {
        configureMojo(scoreMojo);
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
    @InjectMojo(goal = "score")
    void thatARiskScoreEqualToTheThresholdDoesNothing(ScoreMojo scoreMojo) {
        configureMojo(scoreMojo);
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(3);

        assertDoesNotThrow(
                () -> {
                    scoreMojo.execute();
                },
                "Exception not expected");
    }

    @Test
    @InjectMojo(goal = "score")
    void thatFailureToGetARiskScoreEqualThrowsAnException(ScoreMojo scoreMojo) {
        configureMojo(scoreMojo);
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(3);

        assertDoesNotThrow(
                () -> {
                    scoreMojo.execute();
                },
                "Exception not expected");
    }

    @Test
    @InjectMojo(goal = "score")
    void thatARiskScoreLowerThanTheThresholdDoesNothing(ScoreMojo scoreMojo) {
        configureMojo(scoreMojo);
        // The current project score in the JSON file is 3
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));

        scoreMojo.setInheritedRiskScoreThreshold(999);

        assertDoesNotThrow(
                () -> {
                    scoreMojo.execute();
                },
                "Exception not expected");
    }

    @Test
    @InjectMojo(goal = "score")
    void thatWhenNoMetricsHaveBeenCalculatedThenTheMetricsAreRetrieved(ScoreMojo scoreMojo) throws Exception {
        configureMojo(scoreMojo);
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
    @InjectMojo(goal = "score")
    void thatWhenNoMetricsHaveBeenTheTheCallIsRetriedTheCorrectNumberOfTimes(ScoreMojo scoreMojo) throws Exception {
        configureMojo(scoreMojo);
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
    @InjectMojo(goal = "score")
    void thatWhenFailOnErrorIsFalseAFailureFromToDependencyTrackDoesNotFailTheBuild(ScoreMojo scoreMojo) {
        configureMojo(scoreMojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(notFound()));

        assertDoesNotThrow(
                () -> {
                    scoreMojo.setFailOnError(false);
                    scoreMojo.execute();
                },
                "No exception expected");

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }

    @Test
    @InjectMojo(goal = "score")
    void thatWhenFailOnErrorIsTrueAFailureFromToDependencyTrackDoesFailTheBuild(ScoreMojo scoreMojo) throws Exception {
        configureMojo(scoreMojo);
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
    @InjectMojo(goal = "score")
    void thatWhenFailOnErrorIsFalseAFailureToConnectToDependencyTrackDoesNotFailTheBuild(ScoreMojo scoreMojo) {
        // No Wiremock Stubbing

        assertDoesNotThrow(
                () -> {
                    scoreMojo.setDependencyTrackBaseUrl("http://localghost:80");
                    scoreMojo.setFailOnError(false);
                    scoreMojo.execute();
                },
                "No exception expected");
    }

    @Test
    @InjectMojo(goal = "score")
    void thatWhenFailOnErrorIsTrueAFailureToConnectToDependencyTrackDoesFailTheBuild(ScoreMojo scoreMojo)
            throws Exception {
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
    @InjectMojo(goal = "score")
    void thatTheScoreIsSkippedWhenSkipIsTrue(ScoreMojo scoreMojo) throws Exception {
        scoreMojo.setDependencyTrackBaseUrl(wireMockRuntimeInfo.getHttpBaseUrl());
        scoreMojo.setProjectName("dependency-track");
        scoreMojo.setProjectVersion("3.6.0-SNAPSHOT");
        scoreMojo.setSkip("true");

        scoreMojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }
}
