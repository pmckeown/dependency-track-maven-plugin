package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class ProjectActionTest {

    private static final String UUID_1 = "project-uuid-1";
    private static final String PROJECT_NAME_1 = "projectName1";
    private static final String PROJECT_VERSION_1 = "projectVersion1";
    private static final String UUID_2 = "project-uuid-2";
    private static final String PROJECT_NAME_2 = "projectName2";
    private static final String PROJECT_VERSION_2 = "projectVersion2";

    @InjectMocks
    private ProjectAction getProjectAction;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private Logger logger;

    @Test
    public void thatProjectCanBeRetrievedByNameAndVersion() throws Exception {
        Response<List<Project>> response = aSuccessfulResponse();
        doReturn(response).when(projectClient).getProjects();

        Project project = getProjectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    public void thatExceptionIsThrownWhenConnectionFails() {
        Response<List<Project>> response = aSuccessfulResponse();
        doThrow(UnirestException.class).when(projectClient).getProjects();

        try {
            getProjectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatANotFoundResponseResultsInAnException() {
        doReturn(aNotFoundResponse()).when(projectClient).getProjects();

        try {
            getProjectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatNoProjectsAreFoundAnExceptionIsThrown() {
        doReturn(anEmptySuccessResponse()).when(projectClient).getProjects();

        try {
            getProjectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatRequestedProjectCannotBeFoundAnExceptionIsThrown() {
        Response<List<Project>> response = aSuccessfulResponse();
        doReturn(response).when(projectClient).getProjects();

        try {
            getProjectAction.getProject("missing-project", "unknown-version");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    private Response<List<Project>> aSuccessfulResponse() {
        return new Response<>(200, "OK", true, anOptionalProjectList());
    }

    private Response<List<Project>> anEmptySuccessResponse() {
        return new Response<>(200, "OK", true);
    }

    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false);
    }

    private Optional<List<Project>> anOptionalProjectList() {
        return Optional.of(
                Arrays.asList(new Project(UUID_1, PROJECT_NAME_1, PROJECT_VERSION_1, null),
                        new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null)));
    }

}
