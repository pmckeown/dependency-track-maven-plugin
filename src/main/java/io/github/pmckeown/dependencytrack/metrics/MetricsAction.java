package io.github.pmckeown.dependencytrack.metrics;


import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.lang.String.format;

/**
 * Handles the integration to Dependency Track for getting Metrics
 *
 * @author Paul McKeown
 */
@Singleton
public class MetricsAction {

    private MetricsClient metricsClient;

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    public MetricsAction(MetricsClient metricsClient, CommonConfig commonConfig, Logger logger) {
        this.metricsClient = metricsClient;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    public Metrics getMetrics(Project project) throws DependencyTrackException {
        Callable<Optional<Metrics>> getMetricsCallable = () -> {
            logger.info("Polling for metrics from the Dependency-Track server");
            Response<Metrics> response = metricsClient.getMetrics(project);
            return response.getBody();
        };

        try {
            Status<Optional<Metrics>> status = new CallExecutorBuilder<Optional<Metrics>>()
                    .config(getRetryConfig())
                    .build()
                    .execute(getMetricsCallable);

            Optional<Metrics> body = status.getResult();
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

    private RetryConfig getRetryConfig() {
        PollingConfig pollingConfig = commonConfig.getPollingConfig();
        return new RetryConfigBuilder()
                .withMaxNumberOfTries(pollingConfig.getAttempts())
                .withDelayBetweenTries(pollingConfig.getPause(), ChronoUnit.SECONDS)
                .retryOnReturnValue(Optional.empty())
                .withFixedBackoff()
                .build();
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
