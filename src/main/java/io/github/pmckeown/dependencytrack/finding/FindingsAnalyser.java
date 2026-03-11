package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.finding.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.MEDIUM;
import static io.github.pmckeown.dependencytrack.finding.Severity.UNASSIGNED;

import io.github.pmckeown.dependencytrack.Constants;
import java.util.List;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FindingsAnalyser {
    private static final Logger LOG = LoggerFactory.getLogger(FindingsAnalyser.class);

    private static final String ERROR_TEMPLATE = "Number of {} issues [{}] exceeds the maximum allowed [{}]";

    boolean doNumberOfFindingsBreachPolicy(List<Finding> findings, FindingThresholds findingThresholds) {
        LOG.info("Comparing findings against defined thresholds");

        if (findingThresholds == null) {
            return false;
        }

        boolean policyBreached = false;

        long critical = getCount(findings, CRITICAL);
        long high = getCount(findings, HIGH);
        long medium = getCount(findings, MEDIUM);
        long low = getCount(findings, LOW);
        long unassigned = getCount(findings, UNASSIGNED);

        if (findingThresholds.getCritical() != null && critical > findingThresholds.getCritical()) {
            LOG.warn(ERROR_TEMPLATE, Constants.CRITICAL, critical, findingThresholds.getCritical());
            policyBreached = true;
        }

        if (findingThresholds.getHigh() != null && high > findingThresholds.getHigh()) {
            LOG.warn(ERROR_TEMPLATE, Constants.HIGH, high, findingThresholds.getHigh());
            policyBreached = true;
        }

        if (findingThresholds.getMedium() != null && medium > findingThresholds.getMedium()) {
            LOG.warn(ERROR_TEMPLATE, Constants.MEDIUM, medium, findingThresholds.getMedium());
            policyBreached = true;
        }

        if (findingThresholds.getLow() != null && low > findingThresholds.getLow()) {
            LOG.warn(ERROR_TEMPLATE, Constants.LOW, low, findingThresholds.getLow());
            policyBreached = true;
        }
        if (findingThresholds.getUnassigned() != null && unassigned > findingThresholds.getUnassigned()) {
            LOG.warn(ERROR_TEMPLATE, Constants.UNASSIGNED, unassigned, findingThresholds.getUnassigned());
            policyBreached = true;
        }

        return policyBreached;
    }

    private long getCount(List<Finding> findings, Severity severity) {
        return findings.stream()
                .filter(f -> f.getVulnerability().getSeverity() == severity
                        && !f.getAnalysis().isSuppressed())
                .count();
    }
}
