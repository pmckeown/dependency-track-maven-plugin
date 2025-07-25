package io.github.pmckeown.dependencytrack.upload;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM_TOKEN_UUID;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static kong.unirest.Unirest.get;

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

/**
 * Client for uploading BOMs to Dependency Track
 *
 * @author Paul McKeown
 */
@Singleton
class BomClient {

    private CommonConfig commonConfig;
    private Logger logger;

    @Inject
    BomClient(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    /**
     * Upload a BOM to the Dependency-Track server. The BOM is processed asynchronously after the
     * upload is completed and the response returned. The response contains a token that can be used
     * later to query if the bom that the token relates to has been completely processed.
     *
     * @param bom the request object containing the project details and the Base64 encoded bom.xml
     * @return a response containing a token to later determine if processing the supplied BOM is
     *     completed
     */
    Response<UploadBomResponse> uploadBom(UploadBomRequest bom) {
        RequestBodyEntity requestBodyEntity = Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_BOM)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .body(bom);
        HttpResponse<UploadBomResponse> httpResponse =
                requestBodyEntity.asObject(new GenericType<UploadBomResponse>() {});

        Optional<UploadBomResponse> body;
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

    /**
     * Query the server with a processing token to see if the BOM that it related to has been
     * completely processed.
     *
     * @param token The token that was returned from an Upload BOM call
     * @return a response containing a processing flag. If the flag is true, processing has not yet
     *     completed. If the flag is false, processing is either completed or the token supplied was
     *     invalid.
     */
    Response<BomProcessingResponse> isBomBeingProcessed(String token) {
        final HttpResponse<BomProcessingResponse> httpResponse = get(commonConfig.getDependencyTrackBaseUrl()
                        + V1_BOM_TOKEN_UUID)
                .header("X-Api-Key", commonConfig.getApiKey())
                .routeParam("uuid", token)
                .asObject(new GenericType<BomProcessingResponse>() {});

        Optional<BomProcessingResponse> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }
}
