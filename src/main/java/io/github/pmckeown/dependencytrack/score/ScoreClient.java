package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackClient;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;

class ScoreClient extends AbstractDependencyTrackClient {

    public Response<List<Project>> getProjects(ScoreConfig config) {
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
