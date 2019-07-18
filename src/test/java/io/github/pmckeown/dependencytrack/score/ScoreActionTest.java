package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectClient;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.builders.MetricsBuilder.aDefaultMetrics;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ScoreActionTest {

    private static final Integer INHERITED_RISK_SCORE_THRESHOLD = 3;
    private static final String PROJECT_VERSION = "projectVersion";
    private static final String PROJECT_NAME = "projectName";

    @InjectMocks
    private ScoreAction scoreAction;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private MetricsAction metricsAction;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenAnExceptionOccursGettingProjectsThenAnExceptionIsThrown() {
        doThrow(UnirestException.class).when(projectClient).getProjects();

        try {
            scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenNoProjectsAreFoundThenAnExceptionIsThrown() {
        doReturn(new Response(404, "Not Found", false)).when(projectClient).getProjects();

        try {
            scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenCurrentProjectsIsNotFoundInListThenAnExceptionIsThrown() {
        doReturn(aProjectListResponseWithMetrics()).when(projectClient).getProjects();
        doReturn("unknown-project").when(commonConfig).getProjectName();
        doReturn("1.2.3").when(commonConfig).getProjectVersion();

        try {
            scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
            fail("Exception expected");
        } catch (DependencyTrackException ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenTheCurrentProjectHasMetricsInItThenTheScoreIsReturned() throws Exception {
        doReturn(aProjectListResponseWithMetrics()).when(projectClient).getProjects();
        doReturn(PROJECT_NAME).when(commonConfig).getProjectName();
        doReturn(PROJECT_VERSION).when(commonConfig).getProjectVersion();

        Integer score = scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
        assertThat(score, is(equalTo(100)));

        verifyZeroInteractions(metricsAction);
    }

    @Test
    public void thatWhenTheCurrentProjectHasNoMetricsInItTheyAreRequestedAndThenTheScoreIsReturned() throws Exception {
        doReturn(aProjectListResponseWithoutMetrics()).when(projectClient).getProjects();
        doReturn(aDefaultMetrics().withInheritedRiskScore(100).build()).when(metricsAction).getMetrics(
                any(Project.class));
        doReturn(PROJECT_NAME).when(commonConfig).getProjectName();
        doReturn(PROJECT_VERSION).when(commonConfig).getProjectVersion();

        Integer score = scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
        assertThat(score, is(equalTo(100)));

        verify(metricsAction, times(1)).getMetrics(any(Project.class));
    }

    @Test
    public void thatWhenTheCurrentProjectScoreIsZeroThenTheScoreIsReturned() throws Exception {
        doReturn(aProjectListResponseWithoutMetrics()).when(projectClient).getProjects();
        doReturn(aDefaultMetrics().withInheritedRiskScore(0).build()).when(metricsAction).getMetrics(
                any(Project.class));
        doReturn(PROJECT_NAME).when(commonConfig).getProjectName();
        doReturn(PROJECT_VERSION).when(commonConfig).getProjectVersion();

        Integer score = scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
        assertThat(score, is(equalTo(0)));

        verify(metricsAction, times(1)).getMetrics(any(Project.class));
    }

    private Response<List<Project>> aProjectListResponseWithMetrics() {
        return new Response<List<Project>>(200, "OK", true,
                Optional.of(singletonList(aProjectWithMetrics())));
    }

    private Response<List<Project>> aProjectListResponseWithoutMetrics() {
        return new Response<List<Project>>(200, "OK", true,
                Optional.of(singletonList(aProjectWithoutMetrics())));
    }

    private Project aProjectWithMetrics() {
        return new Project("uuid", PROJECT_NAME, PROJECT_VERSION,
                aDefaultMetrics().withInheritedRiskScore(100).build());
    }

    private Project aProjectWithoutMetrics() {
        return new Project("uuid", PROJECT_NAME, PROJECT_VERSION, null);
    }

}
