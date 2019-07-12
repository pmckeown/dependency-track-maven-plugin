package io.github.pmckeown.rest.client;

import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;

import static io.github.pmckeown.rest.client.ObjectMapperBuilder.relaxedObjectMapper;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;

public abstract class AbstractDependencyTrackClient {

    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }
}
