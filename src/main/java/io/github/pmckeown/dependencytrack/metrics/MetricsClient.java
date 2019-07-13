package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackClient;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.rest.model.Metrics;
import io.github.pmckeown.rest.model.Project;
import io.github.pmckeown.rest.model.ResponseWithOptionalBody;
import io.github.pmckeown.util.Logger;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;

import java.util.Optional;

import static io.github.pmckeown.rest.ResourceConstants.V1_CURRENT_PROJECT_METRICS;
import static kong.unirest.Unirest.get;

public class MetricsClient extends AbstractDependencyTrackClient {

    public ResponseWithOptionalBody<Metrics> getMetrics(CommonConfig config, Logger logger, Project project) {
        logger.debug("Getting metrics for project: %s", project.getUuid());
        final HttpResponse<Metrics> httpResponse = get(config.getDependencyTrackBaseUrl() + V1_CURRENT_PROJECT_METRICS)
                .header("X-Api-Key", config.getApiKey())
                .routeParam("uuid", project.getUuid())
                .asObject(new GenericType<Metrics>(){});

        Optional<Metrics> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new ResponseWithOptionalBody<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
    }
}