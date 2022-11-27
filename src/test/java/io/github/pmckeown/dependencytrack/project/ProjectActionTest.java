package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.bom.BomParser;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private ProjectAction projectAction;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private BomParser bomParser;

    @Mock
    private Logger logger;

    @Test
    public void thatProjectCanBeRetrievedByNameAndVersion() throws Exception {
        doReturn(aSuccessResponse().withBody(aProjectList()).build()).when(projectClient).getProjects();

        Project project = projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    public void thatExceptionIsThrownWhenConnectionFails() {
        doThrow(UnirestException.class).when(projectClient).getProjects();

        try {
            projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatANotFoundResponseResultsInAnException() {
        doReturn(aNotFoundResponse()).when(projectClient).getProjects();

        try {
            projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatNoProjectsAreFoundAnExceptionIsThrown() {
        doReturn(aSuccessResponse().build()).when(projectClient).getProjects();

        try {
            projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatRequestedProjectCannotBeFoundAnExceptionIsThrown() {
        doReturn(aSuccessResponse().withBody(aProjectList()).build()).when(projectClient).getProjects();

        try {
            projectAction.getProject("missing-project", "unknown-version");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenProjectInfoIsUpdatedTrueIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));
        boolean projectInfoUpdated = projectAction.updateProjectInfo(aProjectList().get(0),
                String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
        assertThat(projectInfoUpdated, is(equalTo(true)));
    }

    @Test
    public void thatWhenProjectInfoIsNotUpdatedFalseIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aNotFoundResponse()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));
        boolean projectInfoUpdated = projectAction.updateProjectInfo(aProjectList().get(0),
                String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
        assertThat(projectInfoUpdated, is(equalTo(false)));
    }

    @Test
    public void thatWhenProjectInfoUpdateErrorsAnExceptionIsThrown() {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doThrow(UnirestException.class).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));
        try {
            projectAction.updateProjectInfo(aProjectList().get(0),
                    String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }
    
    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false);
    }

    private List<Project> aProjectList() {
        return Arrays.asList(new Project(UUID_1, PROJECT_NAME_1, PROJECT_VERSION_1, null),
                    new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null));
    }

}
