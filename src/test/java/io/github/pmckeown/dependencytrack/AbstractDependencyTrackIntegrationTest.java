package io.github.pmckeown.dependencytrack;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
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

    protected CommonConfig getCommonConfig() {
        return new CommonConfig(
                PROJECT_NAME,
                PROJECT_VERSION,
                HOST + wireMockRule.port(),
                API_KEY);
    }

}
