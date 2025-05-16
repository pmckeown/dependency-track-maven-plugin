package io.github.pmckeown.dependencytrack.suppressions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_ANALYSIS_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.fixType1Analysis;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisClientTest extends AbstractDependencyTrackIntegrationTest {

    @InjectMocks
    private AnalysisClient analysisClient;

    @Mock
    private Logger logger;

    @Before
    public void setup() {
        analysisClient = new AnalysisClient(getCommonConfig(), logger);
    }

    @Test
    public void thatAnalysisCouldBeUploaded() throws Exception {
        stubFor(put(urlPathMatching(V1_ANALYSIS_PROJECT_UUID)).willReturn(
            aResponse().withBody(asJson(fixType1Analysis().build()))));

        Response<UploadAnalysisResponse> response = analysisClient.uploadAnalysis(
            fixType1Analysis().build().getProjectUuid(), fixType1Analysis().build());

        verify(1, putRequestedFor(urlPathMatching(V1_ANALYSIS_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(true)));
        Optional<UploadAnalysisResponse> body = response.getBody();
        if (body.isPresent()) {
            UploadAnalysisResponse analysisResponse = body.get();
            assertThat(analysisResponse, is(not(nullValue())));
            assertThat(analysisResponse.getIsSuppressed(), is(equalTo(fixType1Analysis().build().getSuppressed())));
            assertThat(analysisResponse.getAnalysisResponse(), is(equalTo(fixType1Analysis().build().getAnalysisResponse())));
        } else {
            fail("Body missing");
        }

    }

    @Test
    public void thatSererErrorResultsInUnsuccessfulResponse() {
        stubFor(put(urlPathMatching(V1_ANALYSIS_PROJECT_UUID)).willReturn(badRequest()));

        Response<UploadAnalysisResponse> response = analysisClient.uploadAnalysis(
            fixType1Analysis().build().getProjectUuid(), fixType1Analysis().build());

        verify(1, putRequestedFor(urlPathMatching(V1_ANALYSIS_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(false)));
        assertThat(response.getStatus(), is(equalTo(400)));
    }
}
