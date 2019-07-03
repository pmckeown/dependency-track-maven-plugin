package com.pmckeown.rest.client;

import com.pmckeown.rest.ResourceConstants;
import com.pmckeown.rest.model.Bom;
import com.pmckeown.rest.model.Response;
import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import static com.pmckeown.rest.ResourceConstants.V1_BOM;

public class DependencyTrackClient {

    private final String apiKey;
    private String host;

    public DependencyTrackClient(String host, String apiKey) {
        this.host = normaliseHost(host);
        this.apiKey = apiKey;

        Unirest.config().setObjectMapper(new JacksonObjectMapper());
    }

    public Response uploadBom(Bom bom) {
        try {
            HttpResponse<String> response = Unirest.put(host + V1_BOM)
                    .header(HeaderNames.CONTENT_TYPE, "application/json")
                    .header(HeaderNames.ACCEPT, "application/json")
                    .header("X-Api-Key", apiKey)
                    .body(bom)
                    .asString();

            return new Response(response.getStatus(), response.getStatusText(), response.isSuccess());
        } catch (UnirestException ex) {
            return new Response(-1, ex.getMessage(), false);
        }
    }

    String getHost() {
        return host;
    }

    private String normaliseHost(String host) {
        if (StringUtils.endsWith(host,"/")) {
            return StringUtils.stripEnd(host,"/");
        }
        return host;
    }
}
