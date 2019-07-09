package io.github.pmckeown.mojo.score;

import io.github.pmckeown.mojo.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.rest.ResourceConstants;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.TestMojoLoader.loadScoreMojo;
import static io.github.pmckeown.rest.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.rest.client.TestResourceConstants.API_V1_METRICS_PROJECT_CURRENT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Mojo tests for the score goal
 *
 * @author Paul McKeown
 */
public class ScoreMojoTest extends AbstractDependencyTrackMojoTest {

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
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
    }

    @Test
    public void thatARiskScoreHigherThanTheThresholdCausesBuildToFail() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        scoreMojo.setInheritedRiskScoreThreshold(1);

        try {
            scoreMojo.execute();
            fail("Exception expected");
        } catch (MojoFailureException ex) {
            assertNotNull(ex);
        }
    }

    @Test
    public void thatARiskScoreEqualToTheThresholdDoesNothing() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

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
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

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
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        scoreMojo.setProjectName("noMetrics");
        scoreMojo.setProjectVersion("1.0.0");
        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)));
    }

    @Test
    public void thatWhenNoMetricsHaveBeenCalculatedTheGoalFails() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withStatus(404).withBody("The project could not be found.")));

        scoreMojo.setProjectName("noMetrics");
        scoreMojo.setProjectVersion("1.0.0");

        try {
            scoreMojo.execute();
            fail("Exception expected");
        } catch (MojoExecutionException ex) {
            assertNotNull(ex);
        }
    }
}
