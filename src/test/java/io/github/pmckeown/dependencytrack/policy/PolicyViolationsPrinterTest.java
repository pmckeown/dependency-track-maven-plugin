package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PolicyViolationsPrinterTest {

    @InjectMocks
    private PolicyViolationsPrinter policyViolationsPrinter;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenNoViolationsAreRetrievedThatIsLogged() {
        // Act
        Project project = aProject().withName("X").build();
        policyViolationsPrinter.printPolicyViolations(project, null);

        // Assert
        verify(logger).info("No policy violations were retrieved for project: %s", "X");
    }

    @Test
    public void thatAPolicyViolationIsPrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        List<PolicyViolation> policyViolations = policyViolationsList("SEVERITY", "p1", "INFO");
        policyViolationsPrinter.printPolicyViolations(project, policyViolations);

        verify(logger, times(2)).info(DELIMITER);
        verify(logger).info("%d policy violation(s) were retrieved for project: %s", 1, "a");
        verify(logger).info("Printing policy violations for project %s-%s", "a", "1");
        verify(logger).info("Policy name: %s (%s)", "p1", "INFO");
    }

    @Test
    public void thatMultiplePolicyViolationsArePrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        List<PolicyViolation> policyViolations = policyViolationsList("SEVERITY", "p1", "INFO");
        policyViolations.addAll(policyViolationsList("SEVERITY", "p2", "WARN"));
        policyViolationsPrinter.printPolicyViolations(project, policyViolations);

        verify(logger, times(3)).info(DELIMITER);
        verify(logger).info("%d policy violation(s) were retrieved for project: %s", 2, "a");
        verify(logger).info("Printing policy violations for project %s-%s", "a", "1"); // Intro
        verify(logger).info("Policy name: %s (%s)", "p1", "INFO");
        verify(logger).info("Policy name: %s (%s)", "p2", "WARN");
    }

    private List<PolicyViolation> policyViolationsList(String riskType, String policyName, String violationState) {
        return aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType(riskType)
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy(policyName, violationState)))
                        .withComponent(aComponent()))
                .build();
    }
}
