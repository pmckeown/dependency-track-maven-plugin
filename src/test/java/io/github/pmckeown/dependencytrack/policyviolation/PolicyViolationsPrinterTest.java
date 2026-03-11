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
import io.github.pmckeown.test.logging.SpyLoggerRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class PolicyViolationsPrinterTest {

    @InjectMocks
    private PolicyViolationsPrinter policyViolationsPrinter;

    @Test
    void thatWhenNoViolationsAreRetrievedThatIsLogged() {
        // Act
        Project project = aProject().withName("X").build();
        policyViolationsPrinter.printPolicyViolations(project, null);

        // Assert
        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsPrinter.class);
        verify(logger).info("No policy violations were retrieved for project: {}", "X");
    }

    @Test
    void thatAPolicyViolationIsPrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        List<PolicyViolation> policyViolations = policyViolationsList("SEVERITY", "p1", ViolationState.INFO);
        policyViolationsPrinter.printPolicyViolations(project, policyViolations);

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsPrinter.class);
        verify(logger, times(2)).info(DELIMITER);
        verify(logger).info("{} policy violation(s) were retrieved for project: {}", 1, "a");
        verify(logger).info("Printing policy violations for project {}-{}", "a", "1");
        verify(logger).info("Policy name: {} ({})", "p1", "INFO");
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsPrinter.class);
        verify(logger, times(3)).info(DELIMITER);
        verify(logger).info("{} policy violation(s) were retrieved for project: {}", 2, "a");
        verify(logger).info("Printing policy violations for project {}-{}", "a", "1"); // Intro
        verify(logger).info("Policy name: {} ({})", "p1", "INFO");
        verify(logger).info("Policy name: {} ({})", "p2", "WARN");
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
