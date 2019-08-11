package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.github.pmckeown.dependencytrack.builders.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.builders.ResponseBuilder.aNotFoundResponse;
import static io.github.pmckeown.dependencytrack.builders.ResponseBuilder.aSuccessResponse;
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

    @InjectMocks
    private ProjectAction projectAction;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenProjectIsDeletedThenTrueIsReturn() throws Exception {
        doReturn(aSuccessResponse().build()).when(projectClient).deleteProject(any(Project.class));

        boolean deleted = projectAction.deleteProject(aProject().build());

        assertThat(deleted, is(equalTo(true)));
    }

    @Test
    public void thatWhenProjectIsNotDeletedThenFalseIsReturn() throws Exception {
        doReturn(aNotFoundResponse().build()).when(projectClient).deleteProject(any(Project.class));

        boolean deleted = projectAction.deleteProject(aProject().build());

        assertThat(deleted, is(equalTo(false)));
    }

    @Test
    public void thatWhenAnExceptionOccursWhenDeletingProjectThenCorrectExceptionIsThrown() throws Exception {
        doThrow(UnirestException.class).when(projectClient).deleteProject(any(Project.class));

        try {
            projectAction.deleteProject(aProject().build());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }

        verify(logger, atLeastOnce()).error(anyString(), any(UnirestException.class));
    }

}
