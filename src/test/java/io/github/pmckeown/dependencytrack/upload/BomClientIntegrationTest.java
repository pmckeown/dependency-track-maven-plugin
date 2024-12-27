package io.github.pmckeown.dependencytrack.upload;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.UnirestException;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_BOM_TOKEN_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.upload.BomProcessingResponseBuilder.aBomProcessingResponse;
import static io.github.pmckeown.dependencytrack.upload.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class BomClientIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    private static final String BASE_64_ENCODED_BOM = "blah";

    private BomClient client;

    @Before
    public void setup() {
        client = new BomClient(getCommonConfig());
    }

    @Test
    public void thatBomCanBeUploadedAndProcessingTokenIsReceived() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok().withBody(
                asJson(anUploadBomResponse().withToken("123").build()))));

        Response<UploadBomResponse> response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));
        assertThat(response.getBody().get().getToken(), is(equalTo("123")));

        verify(1, putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponse() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(status(418)));

        Response<UploadBomResponse> response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(1, putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponse() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(notFound()));

        Response<UploadBomResponse> response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(404)));

        verify(1, putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatWhenBomIsStillBeingProcessedThenTheProcessingFlagIsTrue() throws Exception {
        stubFor(get(urlPathMatching(V1_BOM_TOKEN_UUID)).willReturn(ok().withBody(
                asJson(aBomProcessingResponse().withProcessing(true).build()))));

        Response<BomProcessingResponse> response = client.isBomBeingProcessed("123");

        assertThat(response.getBody().get().isProcessing(), is(equalTo(true)));
    }

    @Test
    public void thatWhenBomProcessingIsFinishedThenTheProcessingFlagIsFalse() throws Exception {
        stubFor(get(urlPathMatching(V1_BOM_TOKEN_UUID)).willReturn(ok().withBody(
                asJson(aBomProcessingResponse().withProcessing(false).build()))));

        Response<BomProcessingResponse> response = client.isBomBeingProcessed("123");

        assertThat(response.getBody().get().isProcessing(), is(equalTo(false)));
    }

    @Test
    public void thatWhenAnErrorOccursWhileQueryingTheAUnirestExceptionIsThrown() {
        stubFor(get(urlPathMatching(V1_BOM_TOKEN_UUID)).willReturn(
                aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        try {
            client.isBomBeingProcessed("123");
            fail("UnirestException expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(UnirestException.class)));
        }
    }

    /*
     * Helper methods
     */

    private UploadBomRequest aBom() {
        return new UploadBomRequest(getModuleConfig(), BASE_64_ENCODED_BOM);
    }
}
