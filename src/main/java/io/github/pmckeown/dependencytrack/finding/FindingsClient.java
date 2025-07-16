package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_FINDING_PROJECT_UUID;
import static kong.unirest.Unirest.get;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;

@Singleton
class FindingsClient {

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    FindingsClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    Response<List<Finding>> getFindingsForProject(Project project) {
        logger.debug("Getting findings for project: %s-%s", project.getName(), project.getVersion());
        final HttpResponse<List<Finding>> httpResponse = get(commonConfig.getDependencyTrackBaseUrl()
                        + V1_FINDING_PROJECT_UUID)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", project.getUuid())
                .asObject(new GenericType<List<Finding>>() {});

        Optional<List<Finding>> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }
}
