package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.Constants;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Severity.MEDIUM;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.UNASSIGNED;

@Singleton
public class FindingsAnalyser {

    static final String ERROR_TEMPLATE = "Number of %s issues [%d] exceeds the maximum allowed [%d]";

    private Logger logger;

    @Inject
    public FindingsAnalyser(Logger logger) {
        this.logger = logger;
    }

    boolean doNumberOfFindingsBreachPolicy(List<Finding> findings, FindingThresholds findingThresholds) {
        logger.info("Comparing findings against defined thresholds");

        boolean policyBreached = false;

        long critical = findings.stream().filter(f -> f.getVulnerability().getSeverity() == CRITICAL
                && !f.getAnalysis().isSuppressed()).count();
        long high = findings.stream().filter(f -> f.getVulnerability().getSeverity() == HIGH
                && !f.getAnalysis().isSuppressed()).count();
        long medium = findings.stream().filter(f -> f.getVulnerability().getSeverity() == MEDIUM
                && !f.getAnalysis().isSuppressed()).count();
        long low = findings.stream().filter(f -> f.getVulnerability().getSeverity() == LOW
                && !f.getAnalysis().isSuppressed()).count();
        long unassigned = findings.stream().filter(f -> f.getVulnerability().getSeverity() == UNASSIGNED
                && !f.getAnalysis().isSuppressed()).count();

        if (findingThresholds.getCritical() != null && critical > findingThresholds.getCritical()) {
            logger.warn(ERROR_TEMPLATE, Constants.CRITICAL, critical, findingThresholds.getCritical());
            policyBreached = true;
        }

        if (findingThresholds.getHigh() != null && high > findingThresholds.getHigh()) {
            logger.warn(ERROR_TEMPLATE, Constants.HIGH, high, findingThresholds.getHigh());
            policyBreached = true;
        }

        if (findingThresholds.getMedium() != null && medium > findingThresholds.getMedium()) {
            logger.warn(ERROR_TEMPLATE, Constants.MEDIUM, medium, findingThresholds.getMedium());
            policyBreached = true;
        }

        if (findingThresholds.getLow() != null && low > findingThresholds.getLow()) {
            logger.warn(ERROR_TEMPLATE, Constants.LOW, low, findingThresholds.getLow());
            policyBreached = true;
        }
        if (findingThresholds.getUnassigned() != null && unassigned > findingThresholds.getUnassigned()) {
            logger.warn(ERROR_TEMPLATE, Constants.UNASSIGNED, unassigned, findingThresholds.getUnassigned());
            policyBreached = true;
        }

        return policyBreached;
    }
}
