package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ObjectMapperFactory.relaxedObjectMapper;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_UUID;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;
import static kong.unirest.Unirest.delete;
import static kong.unirest.Unirest.get;
import static kong.unirest.Unirest.patch;

/**
 * Client for getting Project details from Dependency Track
 *
 * @author Paul McKeown
 */
@Singleton
public class ProjectClient {

    private CommonConfig commonConfig;

    @Inject
    public ProjectClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    public Response<List<Project>> getProjects() {
        HttpResponse<List<Project>> httpResponse = get(commonConfig.getDependencyTrackBaseUrl() + V1_PROJECT)
                .header("X-Api-Key", commonConfig.getApiKey())
                .asObject(new GenericType<List<Project>>(){});

        Optional<List<Project>> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }

    Response<Void> deleteProject(Project project) {
        HttpResponse<?> httpResponse = delete(commonConfig.getDependencyTrackBaseUrl() + V1_PROJECT_UUID)
                .routeParam("uuid", project.getUuid())
                .header("X-Api-Key", commonConfig.getApiKey())
                .asEmpty();

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess());
    }

    public Response<?> patchProject(String uuid, ProjectInfo info) {
        HttpResponse<?> httpResponse = patch(commonConfig.getDependencyTrackBaseUrl() + V1_PROJECT_UUID)
                .routeParam("uuid", uuid)
                .header("X-Api-Key", commonConfig.getApiKey())
                .contentType("application/json")
                .body(info)
                .asEmpty();

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess());
    }
}
