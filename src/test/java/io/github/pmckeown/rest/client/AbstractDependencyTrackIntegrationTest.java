package io.github.pmckeown.rest.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;

/**
 * Base class for Dependency Track integrations
 *
 * @author Paul McKeown
 */
public abstract class AbstractDependencyTrackIntegrationTest {

    static final String API_KEY = "api123";
    static final String HOST = "http://localhost:";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    DependencyTrackClient dependencyTrackClient() {
        return new DependencyTrackClient(HOST + wireMockRule.port(), API_KEY);
    }

}
