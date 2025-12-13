package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_METRICS_PROJECT_UUID_CURRENT;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_METRICS_PROJECT_UUID_REFRESH;
import static kong.unirest.Unirest.get;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.Optional;
import javax.inject.Inject;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;

/**
 * Client for getting project Metrics from Dependency Track
 *
 * @author Paul McKeown
 */
class MetricsClient {

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    MetricsClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    Response<Metrics> getMetrics(Project project) {
        logger.debug("Getting metrics for project: %s-%s", project.getName(), project.getVersion());
        final HttpResponse<Metrics> httpResponse = get(commonConfig.getDependencyTrackBaseUrl()
                        + V1_METRICS_PROJECT_UUID_CURRENT)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", project.getUuid())
                .asObject(new GenericType<Metrics>() {});

        Optional<Metrics> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }

    public Response<Void> refreshMetrics(Project project) {
        logger.info("Refreshing Metrics for project: %s-%s", project.getName(), project.getVersion());
        final HttpResponse<?> httpResponse = get(commonConfig.getDependencyTrackBaseUrl()
                        + V1_METRICS_PROJECT_UUID_REFRESH)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", project.getUuid())
                .asEmpty();

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess());
    }
}
