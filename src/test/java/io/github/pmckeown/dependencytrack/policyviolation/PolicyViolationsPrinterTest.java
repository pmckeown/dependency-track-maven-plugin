package io.github.pmckeown.dependencytrack.policyviolation;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class PolicyViolationsPrinterTest {

    @InjectMocks
    private PolicyViolationsPrinter policyViolationsPrinter;

    @Mock
    private Logger logger;

    @Test
    void thatWhenNoViolationsAreRetrievedThatIsLogged() {
        // Act
        Project project = aProject().withName("X").build();
        policyViolationsPrinter.printPolicyViolations(project, null);

        // Assert
        verify(logger).info("No policy violations were retrieved for project: %s", "X");
    }

    @Test
    void thatAPolicyViolationIsPrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        List<PolicyViolation> policyViolations = policyViolationsList("SEVERITY", "p1", ViolationState.INFO);
        policyViolationsPrinter.printPolicyViolations(project, policyViolations);

        verify(logger, times(2)).info(DELIMITER);
        verify(logger).info("%d policy violation(s) were retrieved for project: %s", 1, "a");
        verify(logger).info("Printing policy violations for project %s-%s", "a", "1");
        verify(logger).info("Policy name: %s (%s)", "p1", "INFO");
    }

    @Test
    void thatMultiplePolicyViolationsArePrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        policyViolationsPrinter.printPolicyViolations(
                project,
                aListOfPolicyViolations()
                        .withPolicyViolation(policyViolation("SEVERITY", "p1", ViolationState.INFO))
                        .withPolicyViolation(policyViolation("SEVERITY", "p2", ViolationState.WARN))
                        .build());

        verify(logger, times(3)).info(DELIMITER);
        verify(logger).info("%d policy violation(s) were retrieved for project: %s", 2, "a");
        verify(logger).info("Printing policy violations for project %s-%s", "a", "1"); // Intro
        verify(logger).info("Policy name: %s (%s)", "p1", "INFO");
        verify(logger).info("Policy name: %s (%s)", "p2", "WARN");
    }

    private List<PolicyViolation> policyViolationsList(
            String riskType, String policyName, ViolationState violationState) {
        return aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType(riskType)
                        .withPolicyCondition(aPolicyCondition().withPolicy(new Policy(policyName, violationState)))
                        .withComponent(aComponent()))
                .build();
    }

    private PolicyViolationBuilder policyViolation(String riskType, String policyName, ViolationState violationState) {
        return aPolicyViolation()
                .withType(riskType)
                .withPolicyCondition(aPolicyCondition().withPolicy(new Policy(policyName, violationState)))
                .withComponent(aComponent());
    }
}
