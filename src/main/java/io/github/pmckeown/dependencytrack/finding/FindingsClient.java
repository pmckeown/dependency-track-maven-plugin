package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_ANALYSIS;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_FINDING_PROJECT_UUID;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static kong.unirest.Unirest.get;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Singleton
class FindingsClient {

	private CommonConfig commonConfig;

	private Logger logger;

	@Inject
	FindingsClient(final CommonConfig commonConfig, final Logger logger) {
		this.commonConfig = commonConfig;
		this.logger = logger;
	}

	Response<List<Finding>> getFindingsForProject(final Project project) {
		logger.debug("Getting findings for project: %s-%s", project.getName(), project.getVersion());
		final HttpResponse<List<Finding>> httpResponse = get(
				commonConfig.getDependencyTrackBaseUrl() + V1_FINDING_PROJECT_UUID)
				.header("X-Api-Key", commonConfig.getApiKey()).routeParam("uuid", project.getUuid())
				.asObject(new GenericType<List<Finding>>() {
				});

		Optional<List<Finding>> body;
		if (httpResponse.isSuccess()) {
			body = Optional.of(httpResponse.getBody());
		} else {
			body = Optional.empty();
		}

		return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
	}

	Response<Void> recordNewAnalysisDecision(final AnalysisDecisionRequest analysisDecision) {
		logger.debug("Recording analysis decision: %s", analysisDecision.toString());
		final HttpResponse<?> httpResponse = Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_ANALYSIS)
				.header(CONTENT_TYPE, "application/json").header("X-Api-Key", commonConfig.getApiKey())
				.body(analysisDecision).asEmpty();

		if (!httpResponse.isSuccess()) {
			logger.error("An error occurred while suppressing vulnerability: %d-%s", httpResponse.getStatus(),
					httpResponse.getStatusText());
		}

		return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess());
	}
}
