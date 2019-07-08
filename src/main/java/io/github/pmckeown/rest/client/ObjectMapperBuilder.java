package io.github.pmckeown.rest.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to build {@link ObjectMapper} instances to be passed to Unirest
 *
 * @author Paul McKeown
 */
class ObjectMapperBuilder {

    /**
     * Get an {@link ObjectMapper} instance that is configured to
     * @return
     */
    static ObjectMapper relaxedObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
