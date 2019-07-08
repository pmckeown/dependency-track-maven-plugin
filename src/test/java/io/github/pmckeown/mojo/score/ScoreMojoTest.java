package io.github.pmckeown.mojo.score;

import io.github.pmckeown.mojo.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.rest.ResourceConstants;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.TestMojoLoader.loadScoreMojo;
import static io.github.pmckeown.rest.ResourceConstants.V1_PROJECT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ScoreMojoTest extends AbstractDependencyTrackMojoTest {

    private MavenProjectStub mavenProject = new MavenProjectStub();

    @Before
    public void setup() {
        mavenProject.setArtifactId("dependency-track");
        mavenProject.setVersion("3.6.0-SNAPSHOT");
    }

    @Test
    public void thatAllProjectsCanBeRetrieved() throws Exception {
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        ScoreMojo scoreMojo = loadScoreMojo(mojoRule);
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        scoreMojo.setMavenProject(mavenProject);
        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
    }

    @Test
    public void thatARiskScoreHigherThanTheThresholdCausesBuildToFail() throws Exception {
        // The current project score in the JSON file is 3
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        ScoreMojo scoreMojo = loadScoreMojo(mojoRule);
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        scoreMojo.setMavenProject(mavenProject);
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

        ScoreMojo scoreMojo = loadScoreMojo(mojoRule);
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        scoreMojo.setMavenProject(mavenProject);
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

        ScoreMojo scoreMojo = loadScoreMojo(mojoRule);
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        scoreMojo.setMavenProject(mavenProject);
        scoreMojo.setInheritedRiskScoreThreshold(999);

        try {
            scoreMojo.execute();
        } catch (MojoFailureException ex) {
            fail("Exception not expected");
        }
    }
}
