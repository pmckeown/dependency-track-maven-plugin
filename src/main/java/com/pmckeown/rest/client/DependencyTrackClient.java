package com.pmckeown.rest.client;

import com.pmckeown.rest.model.Bom;
import com.pmckeown.rest.model.Response;
import kong.unirest.*;

public class DependencyTrackClient {

    private static final String V1_BOM = "/v1/bom";

    private String host;
    private String apiKey;

    public DependencyTrackClient(String host, String apiKey) {
        this.host = host;
        this.apiKey = apiKey;

        Unirest.config().setObjectMapper(new JacksonObjectMapper());
    }

    public Response uploadBom(Bom bom) {
        try {
            HttpResponse<String> response = Unirest.put(host + V1_BOM)
                    .header(HeaderNames.CONTENT_TYPE, "application/json")
                    .header(HeaderNames.ACCEPT, "application/json")
                    .header(" X-Api-Key", apiKey)
                    .body(bom)
                    .asString();

            return new Response(response.getStatus(), response.getStatusText(), response.isSuccess());
        } catch (UnirestException ex) {
            return new Response(-1, ex.getMessage(), false);
        }
    }
}
