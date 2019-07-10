package io.github.pmckeown.rest.client;

import io.github.pmckeown.rest.model.Metrics;
import io.github.pmckeown.rest.model.ResponseWithOptionalBody;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.rest.client.TestResourceConstants.API_V1_METRICS_PROJECT_CURRENT;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test metrics retrieval
 *
 * @author Paul McKeown
 */
public class GetMetricsIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    @Test
    public void thatMetricsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)).willReturn(
                aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        ResponseWithOptionalBody<Metrics> metrics = dependencyTrackClient().getMetrics("123");

        assertThat(metrics, is(not(nullValue())));
        verify(exactly(1), getRequestedFor(urlPathMatching(API_V1_METRICS_PROJECT_CURRENT)));
    }
}
