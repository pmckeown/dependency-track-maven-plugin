package io.github.pmckeown.dependencytrack.metrics;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_METRICS_PROJECT_CURRENT;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_METRICS_PROJECT_REFRESH;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@WireMockTest
class MetricsClientTest {

    @InjectMocks
    private MetricsClient metricsClient;

    @Mock
    private Logger logger;

    @Mock
    private CommonConfig commonConfig;

    @Test
    void thatMetricsAreReturnedCorrectly(WireMockRuntimeInfo wmri) {
        doReturn("http://localhost:" + wmri.getHttpPort()).when(commonConfig).getDependencyTrackBaseUrl();
        doReturn("api123").when(commonConfig).getApiKey();
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_CURRENT))
                .willReturn(aResponse().withBodyFile("api/v1/metrics/project/project-metrics.json")));

        Response<Metrics> metricsResponse = metricsClient.getMetrics(aProject().build());

        Optional<Metrics> metricsOptional = metricsResponse.getBody();
        assertThat(metricsOptional.isPresent(), is(equalTo(true)));

        Metrics metrics = metricsOptional.get();
        assertThat(metrics.getInheritedRiskScore(), is(equalTo(3)));
        assertThat(metrics.getCritical(), is(equalTo(0)));
        assertThat(metrics.getHigh(), is(equalTo(0)));
        assertThat(metrics.getMedium(), is(equalTo(1)));
        assertThat(metrics.getLow(), is(equalTo(0)));
        assertThat(metrics.getUnassigned(), is(equalTo(0)));
        assertThat(metrics.getVulnerabilities(), is(equalTo(1)));
        assertThat(metrics.getVulnerableComponents(), is(equalTo(1)));
        assertThat(metrics.getComponents(), is(equalTo(151)));
        assertThat(metrics.getSuppressed(), is(equalTo(0)));
        assertThat(metrics.getFindingsTotal(), is(equalTo(1)));
        assertThat(metrics.getFindingsAudited(), is(equalTo(0)));
        assertThat(metrics.getFindingsUnaudited(), is(equalTo(1)));
        assertThat(metrics.getFirstOccurrence(), is(instanceOf(Date.class)));
        assertThat(metrics.getLastOccurrence(), is(instanceOf(Date.class)));
    }

    @Test
    void thatASuccessfulMetricsRefreshReturnsAnEmptyResponse(WireMockRuntimeInfo wmri) {
        doReturn("http://localhost:" + wmri.getHttpPort()).when(commonConfig).getDependencyTrackBaseUrl();
        doReturn("api123").when(commonConfig).getApiKey();
        stubFor(get(urlPathMatching(V1_METRICS_PROJECT_REFRESH)).willReturn(ok()));

        Response<Void> response = metricsClient.refreshMetrics(aProject().build());

        assertThat(response.isSuccess(), is(equalTo(true)));
        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getBody().isPresent(), is(equalTo(false)));
    }
}
