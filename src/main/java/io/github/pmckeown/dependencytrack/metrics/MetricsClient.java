package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_METRICS_PROJECT_UUID_CURRENT;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_METRICS_PROJECT_UUID_REFRESH;
import static kong.unirest.Unirest.get;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import java.util.Optional;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import org.apache.maven.api.di.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for getting project Metrics from Dependency Track
 *
 * @author Paul McKeown
 */
class MetricsClient {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsClient.class);

    private CommonConfig commonConfig;

    @Inject
    MetricsClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    Response<Metrics> getMetrics(Project project) {
        LOG.debug("Getting metrics for project: {}-{}", project.getName(), project.getVersion());
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
        LOG.info("Refreshing Metrics for project: {}-{}", project.getName(), project.getVersion());
        final HttpResponse<?> httpResponse = get(commonConfig.getDependencyTrackBaseUrl()
                        + V1_METRICS_PROJECT_UUID_REFRESH)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", project.getUuid())
                .asEmpty();

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess());
    }
}
