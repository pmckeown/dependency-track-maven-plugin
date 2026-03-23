package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_FINDING_PROJECT_UUID;
import static kong.unirest.Unirest.get;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import java.util.List;
import java.util.Optional;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
class FindingsClient {

    private static final Logger LOG = LoggerFactory.getLogger(FindingsClient.class);

    private CommonConfig commonConfig;

    @Inject
    FindingsClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    Response<List<Finding>> getFindingsForProject(Project project) {
        LOG.debug("Getting findings for project: {}-{}", project.getName(), project.getVersion());
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
