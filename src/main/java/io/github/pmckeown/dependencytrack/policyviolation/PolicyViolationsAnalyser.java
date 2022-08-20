package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class PolicyViolationsAnalyser {

    static final String ERROR_TEMPLATE = "Policy [%s] is violated under component [%s] [%s]";

    private Logger logger;

    @Inject
    public PolicyViolationsAnalyser(Logger logger) {
        this.logger = logger;
    }

    public boolean isAnyPolicyViolationBreached(List<PolicyViolation> policyViolations, boolean failOnWarn) {
        logger.info("Comparing policy violations against defined policy configuration");

        List<PolicyViolation> policyFailures = new ArrayList<>();
        List<PolicyViolation> policyWarnings = new ArrayList<>();
        boolean policyBreached = false;

        policyFailures.addAll(policyViolations.stream().filter(p ->
                p.getPolicyCondition().getPolicy().getViolationState() == ViolationState.FAIL)
                .collect(Collectors.toList()));

        policyWarnings.addAll(policyViolations.stream().filter(p ->
                p.getPolicyCondition().getPolicy().getViolationState() == ViolationState.WARN)
                .collect(Collectors.toList()));

        if (!policyFailures.isEmpty()) {
            logPolicyBreach(policyFailures);
            policyBreached = true;
        }

        if (failOnWarn && !policyWarnings.isEmpty()) {
            logPolicyBreach(policyWarnings);
            policyBreached = true;
        }

        return policyBreached;
    }

    private void logPolicyBreach(List<PolicyViolation> policyViolationsBreached) {
        policyViolationsBreached.forEach(policyViolation ->
            logger.warn(ERROR_TEMPLATE, policyViolation.getPolicyCondition().getPolicy().getName(),
                    policyViolation.getComponent().getName(), policyViolation.getComponent().getVersion()));
    }
}
