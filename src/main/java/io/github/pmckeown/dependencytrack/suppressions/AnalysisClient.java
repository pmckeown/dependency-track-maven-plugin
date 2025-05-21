package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_ANALYSIS;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestParsingException;

/**
 * Handles uploading a single analyse request to Dependency Track.
 *
 * @author Thomas Hucke
 */
@Singleton
public class AnalysisClient {

    private final CommonConfig commonConfig;

    private final Logger logger;

    @Inject
    AnalysisClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<UploadAnalysisResponse> body;

    Response<UploadAnalysisResponse> uploadAnalysis(Analysis analysis) {
        HttpResponse<UploadAnalysisResponse> httpResponse = Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_ANALYSIS )
            .header(CONTENT_TYPE, "application/json")
            .header("X-Api-Key", commonConfig.getApiKey())
            .body(analysis)
            .asObject(UploadAnalysisResponse.class)
            .ifSuccess(response -> body = Optional.of(response.getBody()))
            .ifFailure(response -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Server response body: %s", response.mapError(String.class));
                    logger.debug("Server response status: %s", response.getStatus());
                    logger.debug("Server response statusText: %s", response.getStatusText());
                    logger.debug("Server response isSuccess: %s", response.isSuccess());
                    if (response.getParsingError().isPresent()) {
                        UnirestParsingException ex = response.getParsingError().get();
                        logger.debug("Parsing error OriginalBody: %s", ex.getOriginalBody());
                        logger.debug("Parsing error Message: %s", ex.getMessage());
                        logger.debug("Parsing error Clause: %s", ex.getCause());
                    }
                }
                body = Optional.empty();
            });
        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }

}
