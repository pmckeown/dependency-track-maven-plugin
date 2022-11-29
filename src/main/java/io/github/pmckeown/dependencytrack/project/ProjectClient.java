package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_UUID;
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

    private static final String X_API_KEY = "X-Api-Key";

    private CommonConfig commonConfig;

    @Inject
    public ProjectClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public Response<List<Project>> getProjects() {
        HttpResponse<List<Project>> httpResponse = get(commonConfig.getDependencyTrackBaseUrl() + V1_PROJECT)
                .header(X_API_KEY, commonConfig.getApiKey())
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
                .header(X_API_KEY, commonConfig.getApiKey())
                .asEmpty();

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess());
    }

    public Response<Void> patchProject(String uuid, ProjectInfo info) {
        HttpResponse<?> httpResponse = patch(commonConfig.getDependencyTrackBaseUrl() + V1_PROJECT_UUID)
                .routeParam("uuid", uuid)
                .header(X_API_KEY, commonConfig.getApiKey())
                .contentType("application/json")
                .body(info)
                .asEmpty();

        boolean isSuccess = httpResponse.isSuccess() || httpResponse.getStatus() == HttpStatus.NOT_MODIFIED;
        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), isSuccess);
    }
}
