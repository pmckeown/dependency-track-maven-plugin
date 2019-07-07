package io.github.pmckeown.rest.client;

import io.github.pmckeown.rest.model.Bom;
import io.github.pmckeown.rest.model.GetProjectsResponse;
import io.github.pmckeown.rest.model.Project;
import io.github.pmckeown.rest.model.Response;
import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static io.github.pmckeown.rest.ResourceConstants.V1_BOM;
import static io.github.pmckeown.rest.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.rest.client.ObjectMapperBuilder.relaxedObjectMapper;

/**
 * Client for interacting with the Dependency Track server
 *
 * @author Paul McKeown
 */
public class DependencyTrackClient {

    private static final int CLIENT_EXCEPTION_STATUS = -1;
    private String apiKey;
    private String host;

    private ObjectMapper objectMapper;

    public DependencyTrackClient(String host, String apiKey) {
        this.host = normaliseHost(host);
        this.apiKey = apiKey;

        this.objectMapper = new JacksonObjectMapper(relaxedObjectMapper());
        Unirest.config().setObjectMapper(objectMapper);
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
            return new Response(CLIENT_EXCEPTION_STATUS, ex.getMessage(), false);
        }
    }

    public GetProjectsResponse getProjects() {
        try {
            HttpResponse<List<Project>> response = Unirest.get(host + V1_PROJECT)
                    .header(HeaderNames.ACCEPT, "application/json")
                    .header("X-Api-Key", apiKey)
                    .asObject(new GenericType<List<Project>>(){});

            return new GetProjectsResponse(response.getStatus(), response.getStatusText(), response.isSuccess(),
                    response.getBody());
        } catch (UnirestException ex) {
            return new GetProjectsResponse(CLIENT_EXCEPTION_STATUS, ex.getMessage(), false, null);
        }
    }

    /*
     * Helper methods
     */

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
