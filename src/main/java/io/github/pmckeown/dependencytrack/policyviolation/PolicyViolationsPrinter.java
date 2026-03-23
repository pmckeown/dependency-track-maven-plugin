package io.github.pmckeown.dependencytrack.policyviolation;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;

import io.github.pmckeown.dependencytrack.project.Project;
import java.util.List;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class PolicyViolationsPrinter {
    private static final Logger LOG = LoggerFactory.getLogger(PolicyViolationsPrinter.class);

    public void printPolicyViolations(Project project, List<PolicyViolation> policyViolations) {
        if (!LOG.isInfoEnabled()) {
            return;
        }
        if (policyViolations == null || policyViolations.isEmpty()) {
            LOG.info("No policy violations were retrieved for project: {}", project.getName());
            return;
        }
        LOG.info(DELIMITER);
        LOG.info("{} policy violation(s) were retrieved for project: {}", policyViolations.size(), project.getName());
        LOG.info("Printing policy violations for project {}-{}", project.getName(), project.getVersion());
        policyViolations.forEach(policyViolation -> {
            PolicyCondition policyCondition = policyViolation.getPolicyCondition();
            Policy policy = policyCondition.getPolicy();
            LOG.info(DELIMITER);
            LOG.info(
                    "Policy name: {} ({})",
                    policy.getName(),
                    policy.getViolationState().name());
            LOG.info(
                    "Policy condition: \"subject == {} && value {} {}\"",
                    policyCondition.getSubject(),
                    policyCondition.getOperator(),
                    policyCondition.getValue());
            LOG.info(
                    "Risk type: {}, Component: {} {}",
                    policyViolation.getType(),
                    policyViolation.getComponent().getName(),
                    policyViolation.getComponent().getVersion());
            LOG.info(""); // Spacer
        });
    }
}
