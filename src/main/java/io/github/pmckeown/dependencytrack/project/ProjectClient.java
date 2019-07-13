package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.CommonConfig;
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
public class ProjectClient {

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    public Response<List<Project>> getProjects(CommonConfig config) {
        HttpResponse<List<Project>> httpResponse = Unirest.get(config.getDependencyTrackBaseUrl() + V1_PROJECT)
                .header("X-Api-Key", config.getApiKey())
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
