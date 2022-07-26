package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.dependencytrack.finding.Component;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.Vulnerability;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.joinWith;

@Singleton
public
class PolicyViolationsPrinter {

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
        logger.info("%d policy violation(s) were retrieved for project: %s", policyViolations.size(), project.getName());
        logger.info("Printing policy violations for project %s-%s", project.getName(), project.getVersion());
        policyViolations.forEach(policyViolation -> {
            PolicyCondition policyCondition = policyViolation.getPolicyCondition();
            Policy policy = policyCondition.getPolicy();
            logger.info(DELIMITER);
            logger.info("%s", policyCondition.toString());
            logger.info("%s: %s", policy.getName(), policy.getViolationState());
            logger.info(""); // Spacer
//            List<String> wrappedDescriptionParts = splitString(vulnerability.getDescription());
//            if (wrappedDescriptionParts != null && !wrappedDescriptionParts.isEmpty()) {
//                wrappedDescriptionParts.forEach(s -> logger.info(s));
//            }
//            if (finding.getAnalysis().isSuppressed()) {
//                logger.info("");
//                logger.info("Suppressed - %s", finding.getAnalysis().getState().name());
//            }
        });
    }
}
