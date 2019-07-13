package io.github.pmckeown.dependencytrack;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.github.pmckeown.dependencytrack.builders.CommonConfigBuilder;
import io.github.pmckeown.rest.client.DependencyTrackClient;
import org.junit.Rule;

/**
 * Base class for Dependency Track integrations
 *
 * @author Paul McKeown
 */
public abstract class AbstractDependencyTrackIntegrationTest {

    protected static final String PROJECT_VERSION = "1.0";
    protected static final String PROJECT_NAME = "testProject";
    protected static final String API_KEY = "api123";
    protected static final String HOST = "http://localhost:";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Deprecated
    public DependencyTrackClient dependencyTrackClient() {
        return new DependencyTrackClient(HOST + wireMockRule.port(), API_KEY);
    }

    protected CommonConfig getCommonConfig() {
        return CommonConfigBuilder.config()
                .withProjectName(PROJECT_NAME)
                .withProjectVersion(PROJECT_VERSION)
                .withApiKey(API_KEY)
                .withDependencyTrackBaseUrl(HOST + wireMockRule.port())
                .build();
    }

}