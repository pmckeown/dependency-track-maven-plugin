package io.github.pmckeown.dependencytrack;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import kong.unirest.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;
import org.junit.Rule;

import static io.github.pmckeown.dependencytrack.ObjectMapperFactory.relaxedObjectMapper;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;

/**
 * Base class for Dependency Track integrations
 *
 * @author Paul McKeown
 */
public abstract class AbstractDependencyTrackIntegrationTest {

    /**
     * Initialise Unirest manually as the initialisation in {@link AbstractDependencyTrackMojo} will not get executed
     */
    static {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    protected static final String PROJECT_VERSION = "1.0";
    protected static final String PROJECT_NAME = "testProject";
    protected static final String API_KEY = "api123";
    protected static final String HOST = "http://localhost:";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    protected CommonConfig getCommonConfig() {
        return new CommonConfig(
                PROJECT_NAME,
                PROJECT_VERSION,
                HOST + wireMockRule.port(),
                API_KEY,
                true,
                PollingConfig.disabled());
    }

}
