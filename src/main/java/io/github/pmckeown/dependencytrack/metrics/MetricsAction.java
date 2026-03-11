package io.github.pmckeown.dependencytrack.metrics;

import static java.lang.String.format;

import io.github.pmckeown.dependencytrack.*;
import io.github.pmckeown.dependencytrack.project.Project;
import java.util.Optional;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the integration to Dependency Track for getting Metrics
 *
 * @author Paul McKeown
 */
@Singleton
public class MetricsAction {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsAction.class);

    private MetricsClient metricsClient;

    private Poller<Metrics> poller;

    private CommonConfig commonConfig;

    @Inject
    public MetricsAction(MetricsClient metricsClient, Poller<Metrics> poller, CommonConfig config) {
        this.metricsClient = metricsClient;
        this.poller = poller;
        this.commonConfig = config;
    }

    public Metrics getMetrics(Project project) throws DependencyTrackException {
        try {
            return pollForMetrics(project);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DependencyTrackException(format("Failed to get Metrics for project: %s", project.getUuid()));
        }
    }

    private Metrics pollForMetrics(Project project) throws DependencyTrackException {
        Optional<Metrics> body = poller.poll(commonConfig.getPollingConfig(), () -> {
            LOG.info("Polling for metrics from the Dependency-Track server");
            Response<Metrics> response = metricsClient.getMetrics(project);
            return response.getBody();
        });
        if (body.isPresent()) {
            LOG.debug("Metrics found for project: {}", project.getUuid());
            return body.get();
        } else {
            throw new DependencyTrackException(
                    "No metrics have yet been calculated. Request a metrics analysis " + "in the Dependency Track UI.");
        }
    }

    public void refreshMetrics(Project project) {
        LOG.info("Requesting Metrics analysis for project: {}-{}", project.getName(), project.getVersion());
        try {
            Response<Void> response = metricsClient.refreshMetrics(project);
            if (response.isSuccess()) {
                LOG.debug("Metrics refreshed");
            } else {
                LOG.debug("Metrics refresh failed, response from server: {}", response.getStatusText());
            }
        } catch (Exception ex) {
            // Exception intentionally logged and swallowed
            LOG.error("Failed to refresh metrics with exception: {}", ex.getMessage());
        }
    }
}
