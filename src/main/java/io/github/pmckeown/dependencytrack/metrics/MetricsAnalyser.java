package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;
import static io.github.pmckeown.dependencytrack.Constants.UNASSIGNED;

import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class MetricsAnalyser {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsAnalyser.class);

    static final String ERROR_TEMPLATE = "Number of {} issues [{}] exceeds the maximum allowed [{}]";

    void analyse(Metrics metrics, MetricsThresholds metricThresholds) throws MojoFailureException {
        LOG.info("Comparing project metrics against defined thresholds");

        boolean failed = false;

        if (metricThresholds.getCritical() != null && metrics.getCritical() > metricThresholds.getCritical()) {
            LOG.warn(ERROR_TEMPLATE, CRITICAL, metrics.getCritical(), metricThresholds.getCritical());
            failed = true;
        }

        if (metricThresholds.getHigh() != null && metrics.getHigh() > metricThresholds.getHigh()) {
            LOG.warn(ERROR_TEMPLATE, HIGH, metrics.getHigh(), metricThresholds.getHigh());
            failed = true;
        }

        if (metricThresholds.getMedium() != null && metrics.getMedium() > metricThresholds.getMedium()) {
            LOG.warn(ERROR_TEMPLATE, MEDIUM, metrics.getMedium(), metricThresholds.getMedium());
            failed = true;
        }

        if (metricThresholds.getLow() != null && metrics.getLow() > metricThresholds.getLow()) {
            LOG.warn(ERROR_TEMPLATE, LOW, metrics.getLow(), metricThresholds.getLow());
            failed = true;
        }

        if (metricThresholds.getUnassigned() != null && metrics.getUnassigned() > metricThresholds.getUnassigned()) {
            LOG.warn(ERROR_TEMPLATE, UNASSIGNED, metrics.getUnassigned(), metricThresholds.getUnassigned());
            failed = true;
        }

        if (failed) {
            throw new MojoFailureException("Project metrics exceeded defined metric thresholds");
        }
    }
}
