package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_FINDING_PROJECT_UUID;
import static kong.unirest.Unirest.get;

@Singleton
public class FindingsClient {

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    FindingsClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }


    Response<List<Finding>> getFindingsForProject(Project project) {
        return this.getFindingsForProject(project, false);
    }

    Response<List<Finding>> getFindingsForProject(Project project, boolean suppressed ) {
        logger.debug("Getting findings for project: %s:%s", project.getName(), project.getVersion());
        return getFindingsForProject(project.getUuid(), suppressed);
    }

    public Response<List<Finding>> getFindingsForProject(String projectUuid, boolean suppressed) {

        final HttpResponse<List<Finding>> httpResponse = get(
                commonConfig.getDependencyTrackBaseUrl() + V1_FINDING_PROJECT_UUID)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", projectUuid)
                .queryString("suppressed", suppressed)
                .asObject(new GenericType<List<Finding>>(){});

        Optional<List<Finding>> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
    }
}
