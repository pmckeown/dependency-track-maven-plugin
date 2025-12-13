package io.github.pmckeown.dependencytrack.upload;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM_TOKEN_UUID;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static kong.unirest.Unirest.get;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.ProjectTag;
import io.github.pmckeown.util.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import kong.unirest.ContentType;
import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
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
     * Upload a BOM to the Dependency-Track server. The BOM is processed asynchronously after the upload is completed and the response returned. The response
     * contains a token that can be used later to query if the bom that the token relates to has been completely processed.
     *
     * @param bom the request object containing the project details and a reference to the bom.xml
     * @param uploadWithPut if true the PUT API will be used instead of the POST API
     * @return a response containing a token to later determine if processing the supplied BOM is completed
     */
    Response<UploadBomResponse> uploadBom(UploadBomRequest bom, boolean uploadWithPut) {
        HttpRequest<?> requestBodyEntity;
        if (uploadWithPut) {
            requestBodyEntity = putUploadRequest(bom);
        } else {
            requestBodyEntity = postUploadRequest(bom);
        }
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

    private RequestBodyEntity putUploadRequest(UploadBomRequest bom) {
        return Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_BOM)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .body(bom);
    }

    private MultipartBody postUploadRequest(UploadBomRequest bom) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(UploadBomRequest.class, UploadBomRequestPostMixin.class);
        Map<String, Object> requestFields = objectMapper.convertValue(bom, new TypeReference<Map<String, Object>>() {});

        MultipartBody request = Unirest.post(commonConfig.getDependencyTrackBaseUrl() + V1_BOM)
                .header("X-Api-Key", commonConfig.getApiKey())
                .fields(requestFields);

        if (bom.getBom().isFileReference()) {
            request.field("bom", bom.getBom().getFile(), ContentType.APPLICATION_OCTET_STREAM.toString());
        } else {
            try {
                InputStream inputStream = bom.getBom().getInputStream();
                request.field("bom", inputStream, ContentType.APPLICATION_OCTET_STREAM, "bom.xml");
            } catch (IOException e) {
                logger.debug("Opening an input stream to the BOM reference failed. %s", e.getMessage());
                throw new IllegalStateException("Failure reading BOM source", e);
            }
        }
        return request;
    }

    /**
     * Query the server with a processing token to see if the BOM that it related to has been completely processed.
     *
     * @param token The token that was returned from an Upload BOM call
     * @return a response containing a processing flag. If the flag is true, processing has not yet completed. If the flag is false, processing is either
     * completed or the token supplied was invalid.
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

    /**
     * Jackson mix-in to create payload for the POST API.
     */
    @JsonInclude(Include.NON_NULL)
    static class UploadBomRequestPostMixin {
        @JsonProperty("isLatest")
        Boolean getIsLatest() {
            return Boolean.FALSE;
        }

        @JsonSerialize(converter = TagListConverter.class)
        List<ProjectTag> getProjectTags() {
            return Collections.emptyList();
        }

        @JsonIgnore(/* bom field will be manually added to the request */ )
        BomReference getBom() {
            return null;
        }
    }

    static class TagListConverter extends StdConverter<List<ProjectTag>, String> {
        @Override
        public String convert(List<ProjectTag> value) {
            if (value == null) {
                return null;
            } else {
                return value.stream().map(ProjectTag::getName).collect(Collectors.joining(","));
            }
        }
    }
}
