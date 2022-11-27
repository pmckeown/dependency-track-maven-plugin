package io.github.pmckeown.dependencytrack.policyviolation;

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

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_POLICY_VIOLATION_PROJECT_UUID;
import static kong.unirest.Unirest.get;

@Singleton
class PolicyViolationsClient {

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    PolicyViolationsClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    Response<List<PolicyViolation>> getPolicyViolationsForProject(Project project) {
        logger.debug("Getting policy violations for project: %s-%s", project.getName(), project.getVersion());
        final HttpResponse<List<PolicyViolation>> httpResponse = get(
                commonConfig.getDependencyTrackBaseUrl() + V1_POLICY_VIOLATION_PROJECT_UUID)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", project.getUuid())
                .asObject(new GenericType<List<PolicyViolation>>(){});

        Optional<List<PolicyViolation>> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
    }
}
