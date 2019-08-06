package io.github.pmckeown.dependencytrack.metrics;


import io.github.pmckeown.dependencytrack.*;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Handles the integration to Dependency Track for getting Metrics
 *
 * @author Paul McKeown
 */
@Singleton
public class MetricsAction {

    private MetricsClient metricsClient;

    private PollingConfig pollingConfig;

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    public MetricsAction(MetricsClient metricsClient, PollingConfig pollingConfig, CommonConfig commonConfig,
             Logger logger) {
        this.metricsClient = metricsClient;
        this.pollingConfig = pollingConfig;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    public Metrics getMetrics(Project project) throws DependencyTrackException {
        try {
            Optional<Metrics> body = new Poller<Metrics>(pollingConfig).poll(() -> {
                logger.info("Polling for metrics from the Dependency-Track server");
                Response<Metrics> response = metricsClient.getMetrics(project);
                return response.getBody();
            });
            if (body.isPresent()) {
                logger.debug("Metrics found for project: %s", project.getUuid());
                return body.get();
            } else {
                throw new DependencyTrackException("No metrics have yet been calculated. Request a metrics analysis " +
                        "in the Dependency Track UI.");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new DependencyTrackException(format("Failed to get Metrics for project: %s", project.getUuid()));
        }
    }

    public void refreshMetrics(Project project) {
        logger.info("Requesting Metrics analysis for project: %s-%s", project.getName(), project.getVersion());
        try {
            Response response = metricsClient.refreshMetrics(project);
            if (response.isSuccess()) {
                logger.debug("Metrics refreshed");
            } else {
                logger.debug("Metrics refresh failed, response from server: %s", response.getStatusText());
            }
        } catch (Exception ex) {
            // Exception intentionally logged and swallowed
            logger.error("Failed to refresh metrics with exception: %s", ex.getMessage());
        }
    }

}
