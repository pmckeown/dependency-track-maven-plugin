package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.rest.ResourceConstants;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.TestMojoLoader.loadScoreMojo;
import static io.github.pmckeown.rest.ResourceConstants.V1_PROJECT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ArbitraryProjectDetailsScoreMojoTest extends AbstractDependencyTrackMojoTest {

    private ScoreMojo scoreMojo;

    @Before
    public void setup() throws Exception {
        scoreMojo = loadScoreMojo(mojoRule);
        scoreMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
    }

    @Test
    public void thatAllProjectsCanBeRetrieved() throws Exception {
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        scoreMojo.setProjectName("testName");
        scoreMojo.setProjectVersion("99.99");
        scoreMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
    }

    @Test
    public void thatBuildShouldNotFailIfFailOnErrorFalseAndProjectNotFound() throws Exception {
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        scoreMojo.setFailOnError(false);
        scoreMojo.setProjectName("unknownName");
        scoreMojo.setProjectVersion("000.000");

        try {
            scoreMojo.execute();
        } catch (MojoFailureException ex) {
            fail("No exception expected");
        }

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
    }

    @Test
    public void thatBuildShouldFailIfFailOnErrorTrueAndProjectNotFound() throws Exception {
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        scoreMojo.setFailOnError(true);
        scoreMojo.setProjectName("unknownName");
        scoreMojo.setProjectVersion("000.000");

        try {
            scoreMojo.execute();
            fail("Exception expected");
        } catch (MojoFailureException ex) {
            assertNotNull(ex);
        }

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
    }
}
