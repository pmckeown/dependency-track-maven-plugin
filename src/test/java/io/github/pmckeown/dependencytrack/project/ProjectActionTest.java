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
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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
        doReturn(aSuccessResponse().withBody(project2()).build()).when(projectClient).getProject(anyString(), anyString());

        Project project = projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    public void thatExceptionIsThrownWhenConnectionFails() {
        doThrow(UnirestException.class).when(projectClient).getProject(anyString(), anyString());

        try {
            projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test(expected = DependencyTrackException.class)
    public void thatANotFoundResponseResultsInAnException() throws DependencyTrackException {
        doReturn(aNotFoundResponse()).when(projectClient).getProject(anyString(), anyString());

        projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
    }

    @Test
    public void thatNoProjectsAreFoundAnExceptionIsThrown() {
        doReturn(aSuccessResponse().build()).when(projectClient).getProject(anyString(), anyString());

        try {
            projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test(expected = DependencyTrackException.class)
    public void thatRequestedProjectCannotBeFoundAnExceptionIsThrown() throws DependencyTrackException {
        doReturn(aSuccessResponse().build()).when(projectClient).getProject(anyString(), anyString());

        projectAction.getProject("missing-project", "unknown-version");
    }

    @Test
    public void thatWhenProjectInfoIsUpdatedTrueIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
        boolean projectInfoUpdated = projectAction.updateProject(project1(), updateReq);
        assertThat(projectInfoUpdated, is(equalTo(true)));
    }

    @Test
    public void thatWhenProjectInfoIsNotUpdatedFalseIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aNotFoundResponse()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
        boolean projectInfoUpdated = projectAction.updateProject(project1(), updateReq);
        assertThat(projectInfoUpdated, is(equalTo(false)));
    }

    @Test
    public void thatWhenProjectInfoUpdateErrorsAnExceptionIsThrown() {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doThrow(UnirestException.class).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));
        try {
            UpdateRequest updateReq = new UpdateRequest();
            updateReq.withBomLocation(String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
            projectAction.updateProject(project1(), updateReq);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenProjectParentIsUpdatedTrueIsReturned() throws Exception {
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        assertTrue(projectAction.updateProject(project2(), updateReq.withParent(project1())));
    }

    @Test
    public void thatWhenProjectParentIsNotUpdatedFalseIsReturned() throws Exception {
        doReturn(aNotFoundResponse()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        assertFalse(projectAction.updateProject(project2(), updateReq.withParent(project1())));
    }

    @Test
    public void thatWhenUpdateProjectParentErrorsAnExceptionIsThrown() {
        doThrow(UnirestException.class).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));
        try {
            UpdateRequest updateReq = new UpdateRequest();
            projectAction.updateProject(project1(), updateReq.withParent(project1()));
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenProjectBomAndIsLatestTrueIsProvidedNoExceptionIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
        assertTrue(projectAction.updateProject(project3(), updateReq));
    }

    @Test
    public void thatWhenProjectBomAndIsLatestFalseIsProvidedNoExceptionIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(String.valueOf(new File(BomParser.class.getResource("bom.xml").getPath())));
        assertTrue(projectAction.updateProject(project1(), updateReq));
    }

    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false);
    }

    private Project project1() {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, false);
    }

    private Project project2() {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, false);
    }

    private Project project3() {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, true);
    }

}
