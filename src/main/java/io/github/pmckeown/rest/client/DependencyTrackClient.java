package io.github.pmckeown.rest.client;

import io.github.pmckeown.rest.model.*;
import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static io.github.pmckeown.rest.ResourceConstants.*;
import static io.github.pmckeown.rest.client.ObjectMapperBuilder.relaxedObjectMapper;
import static kong.unirest.HeaderNames.*;

/**
 * Client for interacting with the Dependency Track server
 *
 * @author Paul McKeown
 */
public class DependencyTrackClient {

    private static final int CLIENT_EXCEPTION_STATUS = -1;
    private String host;
    private String apiKey;

    public DependencyTrackClient(String host, String apiKey) {
        this.host = normaliseHost(host);
        this.apiKey = apiKey;

        // TODO - move to class context
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()));
    }
    
    public Response uploadBom(Bom bom) {
        HttpResponse<String> httpResponse = Unirest.put(host + V1_BOM)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", apiKey)
                .header(ACCEPT_ENCODING, "gzip, deflate")
                .header(ACCEPT, "application/json")
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

    public ResponseWithOptionalBody<List<Project>> getProjects() {
        HttpResponse<List<Project>> httpResponse = Unirest.get(host + V1_PROJECT)
                .header("X-Api-Key", apiKey)
                .header(ACCEPT_ENCODING, "gzip, deflate")
                .header(ACCEPT, "application/json")
                .asObject(new GenericType<List<Project>>(){});

        Optional<List<Project>> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new ResponseWithOptionalBody<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
    }

    public ResponseWithOptionalBody<Metrics> getMetrics(String projectUuid) {
        final HttpResponse<Metrics> httpResponse = Unirest.get(host + V1_CURRENT_PROJECT_METRICS)
                .header("X-Api-Key", apiKey)
                .header(ACCEPT_ENCODING, "gzip, deflate")
                .header(ACCEPT, "application/json")
                .routeParam("uuid", projectUuid)
                .asObject(new GenericType<Metrics>(){});

        Optional<Metrics> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new ResponseWithOptionalBody<>(httpResponse.getStatus(), httpResponse.getStatusText(),
                httpResponse.isSuccess(), body);
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
