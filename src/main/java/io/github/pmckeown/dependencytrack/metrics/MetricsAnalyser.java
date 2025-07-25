package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;
import static io.github.pmckeown.dependencytrack.Constants.UNASSIGNED;

import io.github.pmckeown.util.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.maven.plugin.MojoFailureException;

@Singleton
public class MetricsAnalyser {

    static final String ERROR_TEMPLATE = "Number of %s issues [%d] exceeds the maximum allowed [%d]";

    private Logger logger;

    @Inject
    public MetricsAnalyser(Logger logger) {
        this.logger = logger;
    }

    void analyse(Metrics metrics, MetricsThresholds metricThresholds) throws MojoFailureException {
        logger.info("Comparing project metrics against defined thresholds");

        boolean failed = false;

        if (metricThresholds.getCritical() != null && metrics.getCritical() > metricThresholds.getCritical()) {
            logger.warn(ERROR_TEMPLATE, CRITICAL, metrics.getCritical(), metricThresholds.getCritical());
            failed = true;
        }

        if (metricThresholds.getHigh() != null && metrics.getHigh() > metricThresholds.getHigh()) {
            logger.warn(ERROR_TEMPLATE, HIGH, metrics.getHigh(), metricThresholds.getHigh());
            failed = true;
        }

        if (metricThresholds.getMedium() != null && metrics.getMedium() > metricThresholds.getMedium()) {
            logger.warn(ERROR_TEMPLATE, MEDIUM, metrics.getMedium(), metricThresholds.getMedium());
            failed = true;
        }

        if (metricThresholds.getLow() != null && metrics.getLow() > metricThresholds.getLow()) {
            logger.warn(ERROR_TEMPLATE, LOW, metrics.getLow(), metricThresholds.getLow());
            failed = true;
        }

        if (metricThresholds.getUnassigned() != null && metrics.getUnassigned() > metricThresholds.getUnassigned()) {
            logger.warn(ERROR_TEMPLATE, UNASSIGNED, metrics.getUnassigned(), metricThresholds.getUnassigned());
            failed = true;
        }

        if (failed) {
            throw new MojoFailureException("Project metrics exceeded defined metric thresholds");
        }
    }
}
