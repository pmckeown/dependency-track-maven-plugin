package io.github.pmckeown.dependencytrack.upload;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UploadBomClientIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    static final String BASE_64_ENCODED_BOM = "blah";

    private UploadBomClient client = new UploadBomClient();

    @Test
    public void thatBomIsSentAsExpected() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        Response response = client.uploadBom(config(), aBom());

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponse() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(status(418)));

        Response response = client.uploadBom(config(), aBom());

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponse() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM))
                .willReturn(aResponse().withStatus(404).withBody("Not Found")));

        Response response = client.uploadBom(config(), aBom());

        assertThat(response.getStatus(), is(equalTo(404)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    /*
     * Helper methods
     */

    private UploadBomConfig config() {
        CommonConfig commonConfig = getCommonConfig();
        return new UploadBomConfig(commonConfig, "some/file/path");
    }

    private Bom aBom() {
        return new Bom(PROJECT_NAME, PROJECT_VERSION, false, BASE_64_ENCODED_BOM);
    }
}
