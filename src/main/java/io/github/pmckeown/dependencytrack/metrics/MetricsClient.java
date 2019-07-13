package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.score.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;

import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_CURRENT_PROJECT_METRICS;
import static io.github.pmckeown.dependencytrack.builders.ObjectMapperBuilder.relaxedObjectMapper;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;
import static kong.unirest.Unirest.get;

/**
 * Client for getting project Metrics from Dependency Track
 *
 * @author Paul McKeown
 */
class MetricsClient {

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    Response<Metrics> getMetrics(CommonConfig config, Logger logger, Project project) {
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

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
    }
}
