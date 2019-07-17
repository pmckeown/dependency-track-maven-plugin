package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectClient;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class ScoreActionTest {

    private static final Integer INHERITED_RISK_SCORE_THRESHOLD = 3;

    @InjectMocks
    private ScoreAction scoreAction;

    @Mock
    private ProjectClient projectClient;

    private Logger logger = new Logger();

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

    @Ignore
    @Test
    public void thatWhenTheCurrentProjectHasMetricsInItThenNoneAreRequestedTheScoreIsReturned() throws Exception {
        doReturn(aProjectListResponse()).when(projectClient).getProjects();

        Integer score = scoreAction.determineScore(INHERITED_RISK_SCORE_THRESHOLD);
        assertThat(score, is(equalTo(100)));
    }

    private Response<List<Project>> aProjectListResponse() {
        return new Response<List<Project>>(200, "OK", true, Optional.of(asList(aProject())));
    }

    private Project aProject() {
        return new Project("uuid", "", "", aMetrics());
    }

    private Metrics aMetrics() {
        return new Metrics(100);
    }
}
