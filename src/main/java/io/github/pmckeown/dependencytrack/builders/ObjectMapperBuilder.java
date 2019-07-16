package io.github.pmckeown.dependencytrack.builders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to build {@link ObjectMapper} instances to be passed to Unirest
 *
 * @author Paul McKeown
 */
public class ObjectMapperBuilder {

    private ObjectMapperBuilder() {
        // Use factory method
    }

    /**
     * Get an {@link ObjectMapper} instance that is configured to
     * @return
     */
    public static ObjectMapper relaxedObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
