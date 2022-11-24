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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
        doReturn(aSuccessResponse().withBody(aProjectList()).build()).when(projectClient).getProjects();

        Project project = getProjectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);

        assertThat(project, is(not(nullValue())));
        assertThat(project.getUuid(), is(equalTo(UUID_2)));
    }

    @Test
    public void thatExceptionIsThrownWhenConnectionFails() {
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
        doReturn(aSuccessResponse().build()).when(projectClient).getProjects();

        try {
            getProjectAction.getProject(PROJECT_NAME_2, PROJECT_VERSION_2);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatRequestedProjectCannotBeFoundAnExceptionIsThrown() {
        doReturn(aSuccessResponse().withBody(aProjectList()).build()).when(projectClient).getProjects();

        try {
            getProjectAction.getProject("missing-project", "unknown-version");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void projectInfoCreationFromSbom() {
        File bomFile = new File(ProjectActionTest.class.getResource("bom.xml").getFile());
        ProjectInfo info = getProjectAction.createProjectInfo(bomFile).get();
        assertThat(info.getGroup(), is(equalTo("io.github.pmckeown")));
        assertThat(info.getDescription(), is(equalTo("Maven plugin to integrate with a Dependency Track server to submit dependency manifests and gather project metrics.")));
        assertThat(info.getPurl(), is(equalTo("pkg:maven/io.github.pmckeown/dependency-track-maven-plugin@1.2.1-SNAPSHOT?type=maven-plugin")));
        assertThat(info.getClassifier(), is(equalTo("LIBRARY")));
    }

    @Test
    public void thatProjectInfoCreationFromMissingSbomThrowsNoException() {
        assertThat(getProjectAction.createProjectInfo(new File("no-such-file")).isPresent(), is(equalTo(false)));
    }

    @Test
    public void thatProjectInfoCreationFromOldSbomReturnsNoProjectInfo() {
        File bomFile = new File(ProjectActionTest.class.getResource("bom-1.1.xml").getFile());
        assertThat(getProjectAction.createProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }
    
    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false);
    }

    private List<Project> aProjectList() {
        return Arrays.asList(new Project(UUID_1, PROJECT_NAME_1, PROJECT_VERSION_1, null),
                    new Project(UUID_2, PROJECT_NAME_2, PROJECT_VERSION_2, null));
    }

}
