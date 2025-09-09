package io.github.pmckeown.dependencytrack.project;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.bom.BomParser;
import io.github.pmckeown.util.Logger;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class ProjectActionTest {

    private static final String UUID_2 = "project-uuid-2";
    private static final String PROJECT_NAME_2 = "projectName2";
    private static final String PROJECT_VERSION_2 = "projectVersion2";

    @InjectMocks
    private ProjectAction projectAction;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private BomParser bomParser;

    @Mock
    private Logger logger;

    @Test
    void thatProjectCanBeRetrievedByCommonConfig() throws Exception {
        doReturn(aSuccessResponse().withBody(project2()).build())
                .when(projectClient)
                .getProject(anyString(), anyString(), anyString());

        Project project = projectAction.getProject(getModuleConfig());

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    void thatProjectCanBeRetrievedByNameAndVersion() throws Exception {
        doReturn(aSuccessResponse().withBody(project2()).build())
                .when(projectClient)
                .getProject(anyString(), anyString(), anyString());

        Project project = projectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    void thatProjectCanBeRetrievedByUuid() throws Exception {
        doReturn(aSuccessResponse().withBody(project2()).build())
                .when(projectClient)
                .getProject(anyString(), anyString(), anyString());

        Project project = projectAction.getProject(UUID_2);

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    void thatExceptionIsThrownWhenConnectionFails() {
        doThrow(UnirestException.class).when(projectClient).getProject(anyString(), anyString(), anyString());

        try {
            projectAction.getProject(getModuleConfig());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    void thatANotFoundResponseResultsInAnException() {
        assertThrows(DependencyTrackException.class, () -> {
            doReturn(aNotFoundResponse()).when(projectClient).getProject(anyString(), anyString(), anyString());

            projectAction.getProject(getModuleConfig());
        });
    }

    @Test
    void thatNoProjectsAreFoundAnExceptionIsThrown() {
        doReturn(aSuccessResponse().build()).when(projectClient).getProject(anyString(), anyString(), anyString());

        try {
            projectAction.getProject(getModuleConfig());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    void thatRequestedProjectCannotBeFoundAnExceptionIsThrown() {
        assertThrows(DependencyTrackException.class, () -> {
            doReturn(aSuccessResponse().build()).when(projectClient).getProject(anyString(), anyString(), anyString());

            projectAction.getProject(getModuleConfig());
        });
    }

    @Test
    void thatWhenProjectInfoIsUpdatedTrueIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(getBomLocation());
        boolean projectInfoUpdated = projectAction.updateProject(project1(), updateReq);
        assertThat(projectInfoUpdated, is(equalTo(true)));
    }

    @Test
    void thatWhenProjectInfoIsNotUpdatedFalseIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aNotFoundResponse()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(getBomLocation());
        boolean projectInfoUpdated = projectAction.updateProject(project1(), updateReq);
        assertThat(projectInfoUpdated, is(equalTo(false)));
    }

    @Test
    void thatWhenProjectInfoUpdateErrorsAnExceptionIsThrown() {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doThrow(UnirestException.class).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));
        try {
            UpdateRequest updateReq = new UpdateRequest();
            updateReq.withBomLocation(getBomLocation());
            projectAction.updateProject(project1(), updateReq);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    void thatWhenProjectParentIsUpdatedTrueIsReturned() throws Exception {
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        assertTrue(projectAction.updateProject(project2(), updateReq.withParent(project1())));
    }

    @Test
    void thatWhenProjectParentIsNotUpdatedFalseIsReturned() throws Exception {
        doReturn(aNotFoundResponse()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        assertFalse(projectAction.updateProject(project2(), updateReq.withParent(project1())));
    }

    @Test
    void thatWhenUpdateProjectParentErrorsAnExceptionIsThrown() {
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
    void thatWhenProjectBomAndIsLatestTrueIsProvidedNoExceptionIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(getBomLocation());
        assertTrue(projectAction.updateProject(project3(), updateReq));
    }

    @Test
    void thatWhenProjectBomAndIsLatestFalseIsProvidedNoExceptionIsReturned() throws Exception {
        doReturn(Optional.of(new ProjectInfo())).when(bomParser).getProjectInfo(any(File.class));
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        UpdateRequest updateReq = new UpdateRequest();
        updateReq.withBomLocation(getBomLocation());
        assertTrue(projectAction.updateProject(project1(), updateReq));
    }

    @Test
    void thatProjectTagsAreUpdated() throws Exception {
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        Set<String> tags = new HashSet<>();
        tags.add("Backend");
        tags.add("Team-1");

        List<ProjectTag> existingProjectTags = Collections.emptyList();
        UpdateRequest updateReq = new UpdateRequest();
        assertTrue(projectAction.updateProject(projectWithTags(existingProjectTags), updateReq, tags));
    }

    @Test
    void thatProjectTagsAreUpdatedAndMerged() throws Exception {
        doReturn(aSuccessResponse().build()).when(projectClient).patchProject(anyString(), any(ProjectInfo.class));

        Set<String> tags = new HashSet<>();
        tags.add("Backend");
        tags.add("Team-1");

        List<ProjectTag> existingProjectTags = new LinkedList<>();
        existingProjectTags.add(new ProjectTag("Frontend"));
        UpdateRequest updateReq = new UpdateRequest();
        assertTrue(projectAction.updateProject(projectWithTags(existingProjectTags), updateReq, tags));
    }

    private String getBomLocation() {
        URL bom = BomParser.class.getResource("bom.xml");
        assertNotNull(bom, "Missing bom.xml");
        return String.valueOf(new File(bom.getPath()));
    }

    private Response<Void> aNotFoundResponse() {
        return new Response<>(404, "Not Found", false);
    }

    private Project project1() {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, false, Collections.emptyList());
    }

    private Project project2() {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, false, Collections.emptyList());
    }

    private Project project3() {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, true, Collections.emptyList());
    }

    private Project projectWithTags(List<ProjectTag> tags) {
        return new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null, false, tags);
    }

    private ModuleConfig getModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setProjectName(PROJECT_NAME_2);
        moduleConfig.setProjectVersion(PROJECT_VERSION_2);
        return moduleConfig;
    }
}
