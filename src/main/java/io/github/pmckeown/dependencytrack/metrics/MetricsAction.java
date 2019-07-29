package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

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

    private Logger logger;

    @Inject
    public MetricsAction(MetricsClient metricsClient, Logger logger) {
        this.metricsClient = metricsClient;
        this.logger = logger;
    }

    public Metrics getMetrics(Project project) throws DependencyTrackException {
        try {
            Response<Metrics> response = metricsClient.getMetrics(project);

            Optional<Metrics> body = response.getBody();
            if (body.isPresent()) {
                logger.debug("Metrics found for project: %s", project.getUuid());
                logger.info(body.get().toString());
                return body.get();
            } else {
                throw new DependencyTrackException("No metrics have yet been calculated. Request a metrics analysis " +
                        "in the Dependency Track UI.");
            }

        } catch (UnirestException ex) {
            logger.error(ex.getMessage());
            throw new DependencyTrackException(format("Failed to get Metrics for project: %s", project.getUuid()));
        }
    }
}
