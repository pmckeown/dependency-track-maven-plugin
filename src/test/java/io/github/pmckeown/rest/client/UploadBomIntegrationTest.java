package io.github.pmckeown.rest.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.rest.ResourceConstants;
import io.github.pmckeown.rest.model.Bom;
import io.github.pmckeown.rest.model.Response;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test bom uploads
 *
 * @author Paul McKeown
 */
public class UploadBomIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    static final String BASE_64_ENCODED_BOM = "blah";
    static final String PROJECT_VERSION = "1.0";
    static final String PROJECT_NAME = "testProject";

    @Test
    public void thatBomIsSentAsExpected() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        Response response = dependencyTrackClient().uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponse() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(status(418)));

        Response response = dependencyTrackClient().uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponse() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM))
                .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

        Response response = dependencyTrackClient().uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(-1)));

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
