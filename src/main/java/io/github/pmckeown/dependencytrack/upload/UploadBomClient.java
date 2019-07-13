package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackClient;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.ResponseWithOptionalBody;
import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;

import java.util.Optional;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

class UploadBomClient extends AbstractDependencyTrackClient {

    Response uploadBom(UploadBomConfig config, Bom bom) {
        RequestBodyEntity requestBodyEntity = Unirest.put(config.common().getDependencyTrackBaseUrl() + V1_BOM)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", config.common().getApiKey())
                .body(bom);
        HttpResponse<String> httpResponse = requestBodyEntity.asString();

        Optional<String> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new ResponseWithOptionalBody<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
    }
}
