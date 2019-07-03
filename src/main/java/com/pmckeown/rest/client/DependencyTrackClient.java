package com.pmckeown.rest.client;

import com.pmckeown.rest.ResourceConstants;
import com.pmckeown.rest.model.Bom;
import com.pmckeown.rest.model.Response;
import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;

import static com.pmckeown.rest.ResourceConstants.V1_BOM;

public class DependencyTrackClient {

    private String host;

    public DependencyTrackClient(String host, String apiKey) {
        this.host = normaliseHost(host);

        Unirest.config().setObjectMapper(new JacksonObjectMapper());
        Unirest.config().addDefaultHeader(" X-Api-Key", apiKey);
    }

    public Response uploadBom(Bom bom) {
        try {
            HttpResponse<String> response = Unirest.put(host + V1_BOM)
                    .header(HeaderNames.CONTENT_TYPE, "application/json")
                    .header(HeaderNames.ACCEPT, "application/json")
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
