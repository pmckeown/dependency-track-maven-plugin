package io.github.pmckeown.dependencytrack.policyviolation;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PolicyViolationsPrinter {

    private Logger logger;

    @Inject
    public PolicyViolationsPrinter(Logger logger) {
        this.logger = logger;
    }

    public void printPolicyViolations(Project project, List<PolicyViolation> policyViolations) {
        if (policyViolations == null || policyViolations.isEmpty()) {
            logger.info("No policy violations were retrieved for project: %s", project.getName());
            return;
        }
        logger.info(DELIMITER);
        logger.info(
                "%d policy violation(s) were retrieved for project: %s", policyViolations.size(), project.getName());
        logger.info("Printing policy violations for project %s-%s", project.getName(), project.getVersion());
        policyViolations.forEach(policyViolation -> {
            PolicyCondition policyCondition = policyViolation.getPolicyCondition();
            Policy policy = policyCondition.getPolicy();
            logger.info(DELIMITER);
            logger.info(
                    "Policy name: %s (%s)",
                    policy.getName(), policy.getViolationState().name());
            logger.info(
                    "Policy condition: \"subject == %s && value %s %s\"",
                    policyCondition.getSubject(), policyCondition.getOperator(), policyCondition.getValue());
            logger.info(
                    "Risk type: %s, Component: %s %s",
                    policyViolation.getType(),
                    policyViolation.getComponent().getName(),
                    policyViolation.getComponent().getVersion());
            logger.info(""); // Spacer
        });
    }
}
