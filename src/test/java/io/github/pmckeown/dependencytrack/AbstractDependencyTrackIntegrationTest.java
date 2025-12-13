package io.github.pmckeown.dependencytrack;

import static io.github.pmckeown.dependencytrack.ObjectMapperFactory.relaxedObjectMapper;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import kong.unirest.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;

/**
 * Base class for Dependency Track integrations
 *
 * @author Paul McKeown
 */
@WireMockTest
public abstract class AbstractDependencyTrackIntegrationTest {

    /**
     * Initialise Unirest manually as the initialisation in {@link AbstractDependencyTrackMojo} will
     * not get executed
     */
    static {
        Unirest.config()
                .setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                .addDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                .addDefaultHeader(ACCEPT, "application/json");
    }

    protected static final String PROJECT_VERSION = "1.0";
    protected static final String PROJECT_NAME = "testProject";
    protected static final String API_KEY = "api123";
    protected static final String HOST = "http://localhost:";

    protected CommonConfig getCommonConfig(WireMockRuntimeInfo wmri) {
        CommonConfig config = new CommonConfig();
        config.setDependencyTrackBaseUrl(HOST + wmri.getHttpPort());
        config.setApiKey(API_KEY);
        config.setPollingConfig(PollingConfig.disabled());
        return config;
    }

    protected ModuleConfig getModuleConfig() {
        ModuleConfig config = new ModuleConfig();
        config.setProjectName(PROJECT_NAME);
        config.setProjectVersion(PROJECT_VERSION);
        return config;
    }
}
