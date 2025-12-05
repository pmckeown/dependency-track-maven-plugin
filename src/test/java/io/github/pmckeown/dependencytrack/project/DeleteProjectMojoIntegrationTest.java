package io.github.pmckeown.dependencytrack.project;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_PROJECT_UUID;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteProjectMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    DeleteProjectMojo deleteProjectMojo;

    @BeforeEach
    @Basedir(TEST_PROJECT)
    @InjectMojo(goal = "delete-project")
    @MojoParameter(name = "projectName", value = "dependency-track")
    @MojoParameter(name = "projectVersion", value = "3.6.0-SNAPSHOT")
    @MojoParameter(name = "failOnError", value = "false")
    void setUp(DeleteProjectMojo mojo) {
        deleteProjectMojo = mojo;
        configureMojo(deleteProjectMojo);
    }

    @Test
    void thatAProjectCanBeDeleted() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(200)));

        deleteProjectMojo.execute();

        verify(exactly(1), deleteRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }

    @Test
    void thatWhenProjectDeletionFailedAndFailOnErrorFalseThenMojoSucceeds() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(500)));

        deleteProjectMojo.setFailOnError(false);

        assertDoesNotThrow(
                () -> {
                    deleteProjectMojo.execute();
                },
                "No exception expected");
    }

    @Test
    void thatWhenProjectDeletionFailedAndFailOnErrorTrueThenMojoFailureExceptionIsThrown() {
        assertThrows(MojoFailureException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
            stubFor(delete(urlPathMatching(V1_PROJECT_UUID))
                    .willReturn(aResponse().withStatus(500)));

            deleteProjectMojo.setFailOnError(true);

            deleteProjectMojo.execute();
        });
    }

    @Test
    void thatWhenProjectIsNotFoundDeletionIsNotAttempted() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(aResponse()));
        deleteProjectMojo.setProjectName("unknown");
        deleteProjectMojo.setProjectVersion("1.2.3-SNAPSHOT");

        deleteProjectMojo.execute();

        verify(exactly(0), deleteRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }

    @Test
    void thatWhenProjectDeleteErrorsAndFailOnErrorTrueThenMojoExecutionExceptionIsThrown() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        deleteProjectMojo.setFailOnError(true);

        try {
            deleteProjectMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    void thatWhenProjectDeleteErrorsAndFailOnErrorFalseThenMojoSucceeds() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        deleteProjectMojo.setFailOnError(false);

        assertDoesNotThrow(
                () -> {
                    deleteProjectMojo.execute();
                },
                "No exception expected");
    }

    @Test
    void thatDeleteIsSkippedWhenSkipIsTrue() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(200)));

        deleteProjectMojo.setSkip("true");

        deleteProjectMojo.execute();

        verify(exactly(0), deleteRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }
}
