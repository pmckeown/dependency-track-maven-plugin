package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aNotFoundResponse;
import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class PolicyActionTest {

    @InjectMocks
    private PolicyAction policyAction;

    @Mock
    private PolicyClient policyClient;

    @Mock
    private Logger logger;

    @Test
    public void thatPolicyViolationsAreReturned() throws Exception {
        Project project = aProject().build();
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy", ViolationState.INFO)))
                        .withComponent(aComponent()))
                .build();
        doReturn(aSuccessResponse().withBody(policyViolations).build()).when(policyClient).getPolicyViolationsForProject(project);

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
            logger.debug("DependencyTrackException ", ex.getMessage());
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
            logger.debug("DependencyTrackException ", ex.getMessage());
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }
}
