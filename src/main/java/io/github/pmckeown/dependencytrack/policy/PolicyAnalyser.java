package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class PolicyAnalyser {

    private static final String ERROR_TEMPLATE = "Policy [%s] is violated under component [%s] [%s]";
    private static final String THRESHOLD_ERROR_TEMPLATE = "Number of policy violations [%d] exceeds the maximum allowed [%d]";

    private Logger logger;

    @Inject
    public PolicyAnalyser(Logger logger) {
        this.logger = logger;
    }

    public boolean isAnyPolicyViolationBreached(List<PolicyViolation> policyViolations, PolicyConfig policyConfig) {
        logger.info("Comparing policy violations against defined policy configuration");

        if (policyConfig == null) {
            return false;
        }

        List<PolicyViolation> policyViolationsBreached = new ArrayList<>();
        boolean policyBreached = false;

        if (policyConfig.getPolicyName() != null) {
            policyViolationsBreached.addAll(policyViolations.stream().filter(p ->
                    p.getPolicyCondition().getPolicy().getName().toLowerCase()
                            .contains(policyConfig.getPolicyName().toLowerCase()))
                    .collect(Collectors.toList()));
        }

        if (policyConfig.getRiskType() != null) {
            policyViolationsBreached.addAll(policyViolations.stream().filter(p ->
                    p.getType().equalsIgnoreCase(policyConfig.getRiskType()))
                            .collect(Collectors.toList()));
        }

        if (policyConfig.getViolationState() != null) {
            policyViolationsBreached.addAll(policyViolations.stream().filter(p ->
                    p.getPolicyCondition().getPolicy().getViolationState().equalsIgnoreCase(policyConfig.getViolationState()))
                            .collect(Collectors.toList()));
        }

        if (!policyViolationsBreached.isEmpty()) {
            logWarning(policyViolationsBreached);
            policyBreached = true;
        }

        if (policyConfig.getThreshold() != null && policyViolations.size() > policyConfig.getThreshold()) {
                logger.warn(THRESHOLD_ERROR_TEMPLATE, policyViolations.size(), policyConfig.getThreshold());
                policyBreached = true;
        }

        return policyBreached;
    }

    private void logWarning(List<PolicyViolation> policyViolationsBreached) {
        policyViolationsBreached.forEach(policyViolation ->
            logger.warn(ERROR_TEMPLATE, policyViolation.getPolicyCondition().getPolicy().getName(),
                    policyViolation.getComponent().getName(), policyViolation.getComponent().getVersion()));
    }
}
