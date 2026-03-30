package io.github.pmckeown.dependencytrack.project;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_PROJECT_UUID;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;

class DeleteProjectMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    @Test
    @InjectMojo(goal = "delete-project")
    void thatAProjectCanBeDeleted(DeleteProjectMojo deleteProjectMojo) throws Exception {
        configureMojo(deleteProjectMojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/dependency-track-3.6.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(200)));

        deleteProjectMojo.execute();

        verify(exactly(1), deleteRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }

    @Test
    @InjectMojo(goal = "delete-project")
    void thatWhenProjectDeletionFailedAndFailOnErrorFalseThenMojoSucceeds(DeleteProjectMojo deleteProjectMojo) {
        configureMojo(deleteProjectMojo);
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
    @InjectMojo(goal = "delete-project")
    void thatWhenProjectDeletionFailedAndFailOnErrorTrueThenMojoFailureExceptionIsThrown(
            DeleteProjectMojo deleteProjectMojo) {
        configureMojo(deleteProjectMojo);
        assertThrows(DependencyTrackMojoException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
            stubFor(delete(urlPathMatching(V1_PROJECT_UUID))
                    .willReturn(aResponse().withStatus(500)));

            deleteProjectMojo.setFailOnError(true);

            deleteProjectMojo.execute();
        });
    }

    @Test
    @InjectMojo(goal = "delete-project")
    void thatWhenProjectIsNotFoundDeletionIsNotAttempted(DeleteProjectMojo deleteProjectMojo) throws Exception {
        configureMojo(deleteProjectMojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(aResponse()));
        deleteProjectMojo.setProjectName("unknown");
        deleteProjectMojo.setProjectVersion("1.2.3-SNAPSHOT");

        deleteProjectMojo.execute();

        verify(exactly(0), deleteRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }

    @Test
    @InjectMojo(goal = "delete-project")
    void thatWhenProjectDeleteErrorsAndFailOnErrorTrueThenMojoExecutionExceptionIsThrown(
            DeleteProjectMojo deleteProjectMojo) {
        configureMojo(deleteProjectMojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        deleteProjectMojo.setFailOnError(true);

        try {
            deleteProjectMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackMojoException.class)));
        }
    }

    @Test
    @InjectMojo(goal = "delete-project")
    void thatWhenProjectDeleteErrorsAndFailOnErrorFalseThenMojoSucceeds(DeleteProjectMojo deleteProjectMojo) {
        configureMojo(deleteProjectMojo);
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
    @InjectMojo(goal = "delete-project")
    void thatDeleteIsSkippedWhenSkipIsTrue(DeleteProjectMojo deleteProjectMojo) throws Exception {
        configureMojo(deleteProjectMojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(delete(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(200)));

        deleteProjectMojo.setSkip("true");

        deleteProjectMojo.execute();

        verify(exactly(0), deleteRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }
}
