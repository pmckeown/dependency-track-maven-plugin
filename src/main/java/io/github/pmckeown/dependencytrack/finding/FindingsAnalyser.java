package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.Constants;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.MEDIUM;

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

        long critical = findings.stream().filter(f -> f.getVulnerability().getSeverity() == CRITICAL).count();
        long high = findings.stream().filter(f -> f.getVulnerability().getSeverity() == HIGH).count();
        long medium = findings.stream().filter(f -> f.getVulnerability().getSeverity() == MEDIUM).count();
        long low = findings.stream().filter(f -> f.getVulnerability().getSeverity() == LOW).count();

        if (critical > findingThresholds.getCritical()) {
            logger.warn(ERROR_TEMPLATE, Constants.CRITICAL, critical, findingThresholds.getCritical());
            failed = true;
        }

        if (high > findingThresholds.getHigh()) {
            logger.warn(ERROR_TEMPLATE, Constants.HIGH, high, findingThresholds.getHigh());
            failed = true;
        }

        if (medium > findingThresholds.getMedium()) {
            logger.warn(ERROR_TEMPLATE, Constants.MEDIUM, medium, findingThresholds.getMedium());
            failed = true;
        }

        if (low > findingThresholds.getLow()) {
            logger.warn(ERROR_TEMPLATE, Constants.LOW, low, findingThresholds.getLow());
            failed = true;
        }

        if (failed) {
            throw new MojoFailureException("Number of findings exceeded defined thresholds");
        }
    }
}
