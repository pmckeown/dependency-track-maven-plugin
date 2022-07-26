package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ObjectMapperFactory.relaxedObjectMapper;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_POLICY_VIOLATION_PROJECT_UUID;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;
import static kong.unirest.Unirest.get;

@Singleton
class PolicyClient {

    private CommonConfig commonConfig;

    private Logger logger;

    @Inject
    PolicyClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
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
