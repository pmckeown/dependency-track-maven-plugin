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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteProjectActionTest {

    private static final String PROJECT_UUID = "1234";
    private static final String PROJECT_NAME = "test-app";
    private static final String PROJECT_VERSION = "1.2.3";

    @InjectMocks
    private ProjectAction projectAction;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenProjectIsDeletedThenTrueIsReturn() throws Exception {
        doReturn(aSuccessResponse()).when(projectClient).deleteProject(any(Project.class));

        boolean deleted = projectAction.deleteProject(aProject());

        assertThat(deleted, is(equalTo(true)));
    }

    @Test
    public void thatWhenProjectIsNotDeletedThenFalseIsReturn() throws Exception {
        doReturn(aFailedResponse()).when(projectClient).deleteProject(any(Project.class));

        boolean deleted = projectAction.deleteProject(aProject());

        assertThat(deleted, is(equalTo(false)));
    }

    @Test
    public void thatWhenAnExceptionOccursWhenDeletingProjectThenCorrectExceptionIsThrown() throws Exception {
        doThrow(UnirestException.class).when(projectClient).deleteProject(any(Project.class));

        try {
            projectAction.deleteProject(aProject());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }

        verify(logger, atLeastOnce()).error(anyString(), any(UnirestException.class));
    }

    private Project aProject() {
        return new Project(PROJECT_UUID, PROJECT_NAME, PROJECT_VERSION, null);
    }

    private Response<?> aSuccessResponse() {
        return new Response<Optional>(200, "OK", true);
    }

    private Response<?> aFailedResponse() {
        return new Response<Optional>(404, "Not Found", false);
    }
}
