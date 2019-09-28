package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;

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

        if (metrics.getCritical() > nullableInteger(metricThresholds.getCritical())) {
            logger.warn(ERROR_TEMPLATE, CRITICAL, metrics.getCritical(),
                    nullableInteger(metricThresholds.getCritical()));
            failed = true;
        }

        if (metrics.getHigh() > nullableInteger(metricThresholds.getHigh())) {
            logger.warn(ERROR_TEMPLATE, HIGH, metrics.getHigh(), nullableInteger(metricThresholds.getHigh()));
            failed = true;
        }

        if (metrics.getMedium() > nullableInteger(metricThresholds.getMedium())) {
            logger.warn(ERROR_TEMPLATE, MEDIUM, metrics.getMedium(), nullableInteger(metricThresholds.getMedium()));
            failed = true;
        }

        if (metrics.getLow() > nullableInteger(metricThresholds.getLow())) {
            logger.warn(ERROR_TEMPLATE, LOW, metrics.getLow(), nullableInteger(metricThresholds.getLow()));
            failed = true;
        }

        if (failed) {
            throw new MojoFailureException("Project metrics exceeded defined metric thresholds");
        }
    }

    private int nullableInteger(Integer value) {
        return value == null ? 0 : value;
    }
}
