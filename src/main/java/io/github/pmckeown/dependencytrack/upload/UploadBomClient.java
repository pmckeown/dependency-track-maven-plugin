package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.builders.ObjectMapperBuilder.relaxedObjectMapper;
import static kong.unirest.HeaderNames.*;

/**
 * Client for uploading BOMs to Dependency Track
 *
 * @author Paul McKeown
 */
@Singleton
class UploadBomClient {

    private CommonConfig commonConfig;

    @Inject
    UploadBomClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    Response uploadBom(Bom bom) {
        RequestBodyEntity requestBodyEntity = Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_BOM)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .body(bom);
        HttpResponse<String> httpResponse = requestBodyEntity.asString();

        Optional<String> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }
}
