package io.github.pmckeown.dependencytrack.policyviolation;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PolicyViolationsAnalyser {
    private static final Logger LOG = LoggerFactory.getLogger(PolicyViolationsAnalyser.class);

    static final String ERROR_TEMPLATE = "Policy [{}] is violated under component [{}] [{}]";

    public boolean isAnyPolicyViolationBreached(List<PolicyViolation> policyViolations, boolean failOnWarn) {
        LOG.info("Comparing policy violations against defined policy configuration");

        List<PolicyViolation> policyFailures = new ArrayList<>();
        List<PolicyViolation> policyWarnings = new ArrayList<>();
        boolean policyBreached = false;

        policyFailures.addAll(policyViolations.stream()
                .filter(p -> p.getPolicyCondition().getPolicy().getViolationState() == ViolationState.FAIL)
                .toList());

        policyWarnings.addAll(policyViolations.stream()
                .filter(p -> p.getPolicyCondition().getPolicy().getViolationState() == ViolationState.WARN)
                .toList());

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
        policyViolationsBreached.forEach(policyViolation -> LOG.warn(
                ERROR_TEMPLATE,
                policyViolation.getPolicyCondition().getPolicy().getName(),
                policyViolation.getComponent().getName(),
                policyViolation.getComponent().getVersion()));
    }
}
