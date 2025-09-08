package io.github.pmckeown.dependencytrack.policyviolation;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aNotFoundResponse;
import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class PolicyViolationsActionTest {

    @InjectMocks
    private PolicyViolationsAction policyAction;

    @Mock
    private PolicyViolationsClient policyClient;

    @Mock
    private Logger logger;

    @Test
    public void thatPolicyViolationsAreReturned() throws Exception {
        Project project = aProject().build();
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(
                                aPolicyCondition().withPolicy(new Policy("testPolicy", ViolationState.INFO)))
                        .withComponent(aComponent()))
                .build();
        doReturn(aSuccessResponse().withBody(policyViolations).build())
                .when(policyClient)
                .getPolicyViolationsForProject(project);

        List<PolicyViolation> returnedPolicyViolations = policyAction.getPolicyViolations(project);

        assertThat(returnedPolicyViolations.size(), is(equalTo(1)));
        assertThat(returnedPolicyViolations.get(0).getPolicyCondition().getValue(), is(equalTo("IS HIGH")));
    }

    @Test
    public void thatWhenNoViolationsAreReturnedThenAnEmptyListIsReturned() throws Exception {
        Project project = aProject().build();
        doReturn(aSuccessResponse().build()).when(policyClient).getPolicyViolationsForProject(project);

        List<PolicyViolation> policyViolations = policyAction.getPolicyViolations(project);
        assertThat(policyViolations.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void thatAnErrorResponseIsReceivedAnExceptionIsThrown() {
        Project project = aProject().build();
        doReturn(aNotFoundResponse().build()).when(policyClient).getPolicyViolationsForProject(project);

        try {
            policyAction.getPolicyViolations(project);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenAClientExceptionIsEncounteredAnExceptionIsThrown() {
        Project project = aProject().build();
        doThrow(UnirestException.class).when(policyClient).getPolicyViolationsForProject(project);

        try {
            policyAction.getPolicyViolations(project);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }
}
