package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;

import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.dependencytrack.builders.ObjectMapperBuilder.relaxedObjectMapper;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;

/**
 * Client for getting Project details from Dependency Track
 *
 * @author Paul McKeown
 */
class ProjectClient {

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    Response<List<Project>> getProjects(ScoreConfig config) {
        HttpResponse<List<Project>> httpResponse = Unirest.get(config.common().getDependencyTrackBaseUrl() + V1_PROJECT)
                .header("X-Api-Key", config.common().getApiKey())
                .asObject(new GenericType<List<Project>>(){});

        Optional<List<Project>> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }
}
