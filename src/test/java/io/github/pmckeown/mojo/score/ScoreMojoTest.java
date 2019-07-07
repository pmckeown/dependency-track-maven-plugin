package io.github.pmckeown.mojo.score;

import io.github.pmckeown.mojo.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.rest.ResourceConstants;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.TestMojoLoader.loadScoreMojo;
import static io.github.pmckeown.rest.ResourceConstants.V1_PROJECT;

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
}
