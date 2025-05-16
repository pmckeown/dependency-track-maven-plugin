package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_ANALYSIS;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;

@Singleton
public class AnalysisClient {

    private final CommonConfig commonConfig;

    private final Logger logger;

    @Inject
    AnalysisClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    /**
     * Upload a BOM to the Dependency-Track server.  The BOM is processed asynchronously after the upload is completed
     * and the response returned.  The response contains a token that can be used later to query if the bom that the
     * token relates to has been completely processed.
     *
     * @param analysis the request object containing the project details and the Base64 encoded bom.xml
     * @return a response containing a token to later determine if processing the supplied BOM is completed
     */
    Response<UploadAnalysisResponse> uploadAnalysis(String projectUuid, Analysis analysis) {
        RequestBodyEntity requestBodyEntity = Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_ANALYSIS )
            .routeParam("uuid", projectUuid)
            .header(CONTENT_TYPE, "application/json")
            .header("X-Api-Key", commonConfig.getApiKey())
            .body(analysis);
        HttpResponse<UploadAnalysisResponse> httpResponse = requestBodyEntity.asObject(
            new GenericType<UploadAnalysisResponse>() {});

        Optional<UploadAnalysisResponse> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Server response body: %s", httpResponse.mapError(String.class));
            }
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }

}
