package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;

import kong.unirest.UnirestException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Handles the integration to Dependency Track for getting Metrics
 *
 * @author Paul McKeown
 */
@Named
@Singleton
public class MetricsAction {

    private MetricsClient metricsClient = new MetricsClient();

    public Metrics getMetrics(CommonConfig config, Logger logger, Project project) throws DependencyTrackException {
        try {
            Response<Metrics> response = metricsClient.getMetrics(config, logger, project);

            Optional<Metrics> body = response.getBody();
            if (body.isPresent()) {
                logger.debug("Metrics found for project: %s", project.getUuid());
                logger.debug(body.get().toString());
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
