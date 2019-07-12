package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.rest.client.AbstractDependencyTrackClient;
import io.github.pmckeown.rest.model.Bom;
import io.github.pmckeown.rest.model.Response;
import io.github.pmckeown.rest.model.ResponseWithOptionalBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.Optional;

import static io.github.pmckeown.rest.ResourceConstants.V1_BOM;
import static kong.unirest.HeaderNames.*;

class UploadBomClient extends AbstractDependencyTrackClient {

    Response uploadBom(UploadBomConfig config, Bom bom) {
        HttpResponse<String> httpResponse = Unirest.put(config.common().getDependencyTrackBaseUrl() + V1_BOM)
                .header(ACCEPT, "application/json")
                .header("X-Api-Key", config.common().getApiKey())
                .body(bom)
                .asString();

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
