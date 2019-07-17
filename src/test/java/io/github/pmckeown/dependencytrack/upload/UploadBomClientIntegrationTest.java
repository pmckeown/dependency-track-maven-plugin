package io.github.pmckeown.dependencytrack.upload;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import io.github.pmckeown.dependencytrack.Response;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UploadBomClientIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    private static final String BASE_64_ENCODED_BOM = "blah";

    private UploadBomClient client;

    @Before
    public void setup() {
        client = new UploadBomClient(getCommonConfig());
    }

    @Test
    public void thatBomIsSentAsExpected() {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        Response response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponse() {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(status(418)));

        Response response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponse() {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM))
                .willReturn(aResponse().withStatus(404).withBody("Not Found")));

        Response response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(404)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    /*
     * Helper methods
     */

    private Bom aBom() {
        return new Bom(PROJECT_NAME, PROJECT_VERSION, false, BASE_64_ENCODED_BOM);
    }
}
