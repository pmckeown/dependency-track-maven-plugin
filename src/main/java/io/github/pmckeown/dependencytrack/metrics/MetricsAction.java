package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.score.Project;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

import static java.lang.String.format;

public class MetricsAction {

    private MetricsClient metricsClient = new MetricsClient();

    public Metrics getMetrics(CommonConfig config, Logger logger, Project project) throws DependencyTrackException {
        try {
            Response<Metrics> response = metricsClient.getMetrics(config, logger, project);

            if (!response.getBody().isPresent()) {
                throw new DependencyTrackException("No metrics have yet been calculated. Request a metrics analysis " +
                        "in the Dependency Track UI.");
            }
            logger.debug("Metrics found for project: %s", project.getUuid());
            logger.debug(response.getBody().get().toString());
            return response.getBody().get();
        } catch (UnirestException ex) {
            logger.error(ex.getMessage());
            throw new DependencyTrackException(format("Failed to get Metrics for project: %s", project.getUuid()));
        }
    }
}
