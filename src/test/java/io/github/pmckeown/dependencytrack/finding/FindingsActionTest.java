package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aNotFoundResponse;
import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindingsActionTest {

    @InjectMocks
    private FindingsAction findingAction;

    @Mock
    private FindingsClient findingClient;

    @Mock
    private Logger logger;

    @Test
    void thatFindingsAreReturned() throws Exception {
        Project project = aProject().build();
        List<Finding> findings = aListOfFindings()
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability())
                        .withComponent(aComponent()))
                .build();
        doReturn(aSuccessResponse().withBody(findings).build())
                .when(findingClient)
                .getFindingsForProject(project);

        List<Finding> returnedFindings = findingAction.getFindings(project);

        assertThat(returnedFindings.size(), is(equalTo(1)));
        assertThat(returnedFindings.get(0).getAnalysis().isSuppressed(), is(equalTo(false)));
    }

    @Test
    void thatWhenNoFindingsAreReturnedThenAnEmptyListIsReturned() throws Exception {
        Project project = aProject().build();
        doReturn(aSuccessResponse().build()).when(findingClient).getFindingsForProject(project);

        List<Finding> findings = findingAction.getFindings(project);
        assertThat(findings.isEmpty(), is(equalTo(true)));
    }

    @Test
    void thatAnErrorResponseIsReceivedAnExceptionIsThrown() {
        Project project = aProject().build();
        doReturn(aNotFoundResponse().build()).when(findingClient).getFindingsForProject(project);

        try {
            findingAction.getFindings(project);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    void thatWhenAClientExceptionIsEncounteredAnExceptionIsThrown() {
        Project project = aProject().build();
        doThrow(UnirestException.class).when(findingClient).getFindingsForProject(project);

        try {
            findingAction.getFindings(project);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }
}
