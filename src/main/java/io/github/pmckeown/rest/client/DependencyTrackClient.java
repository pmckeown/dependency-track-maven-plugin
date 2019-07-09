package io.github.pmckeown.rest.client;

import io.github.pmckeown.rest.model.*;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
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

    private String host;
    private String apiKey;

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    public DependencyTrackClient(String host, String apiKey) {
        this.host = normaliseHost(host);
        this.apiKey = apiKey;
    }
    
    public Response uploadBom(Bom bom) {
        HttpResponse<String> httpResponse = Unirest.put(host + V1_BOM)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", apiKey)
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
