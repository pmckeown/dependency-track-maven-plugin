package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.Constants;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.MEDIUM;

@Singleton
public class FindingsAnalyser {

    static final String ERROR_TEMPLATE = "Number of %s issues [%d] exceeds the maximum allowed [%d]";

    private Logger logger;

    @Inject
    public FindingsAnalyser(Logger logger) {
        this.logger = logger;
    }

    void analyse(List<Finding> findings, FindingThresholds findingThresholds) throws MojoFailureException {
        logger.info("Comparing findings against defined thresholds");

        boolean failed = false;

        long critical = findings.stream().filter(f -> f.getVulnerability().getSeverity() == CRITICAL
                && !f.getAnalysis().isSuppressed()).count();
        long high = findings.stream().filter(f -> f.getVulnerability().getSeverity() == HIGH
                && !f.getAnalysis().isSuppressed()).count();
        long medium = findings.stream().filter(f -> f.getVulnerability().getSeverity() == MEDIUM
                && !f.getAnalysis().isSuppressed()).count();
        long low = findings.stream().filter(f -> f.getVulnerability().getSeverity() == LOW
                && !f.getAnalysis().isSuppressed()).count();

        if (findingThresholds.getCritical() != null && critical > findingThresholds.getCritical()) {
            logger.warn(ERROR_TEMPLATE, Constants.CRITICAL, critical, findingThresholds.getCritical());
            failed = true;
        }

        if (findingThresholds.getHigh() != null && high > findingThresholds.getHigh()) {
            logger.warn(ERROR_TEMPLATE, Constants.HIGH, high, findingThresholds.getHigh());
            failed = true;
        }

        if (findingThresholds.getMedium() != null && medium > findingThresholds.getMedium()) {
            logger.warn(ERROR_TEMPLATE, Constants.MEDIUM, medium, findingThresholds.getMedium());
            failed = true;
        }

        if (findingThresholds.getLow() != null && low > findingThresholds.getLow()) {
            logger.warn(ERROR_TEMPLATE, Constants.LOW, low, findingThresholds.getLow());
            failed = true;
        }

        if (failed) {
            throw new MojoFailureException("Number of findings exceeded defined thresholds");
        }
    }
}
