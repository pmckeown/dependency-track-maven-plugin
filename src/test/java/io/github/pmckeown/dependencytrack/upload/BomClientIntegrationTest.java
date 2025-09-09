package io.github.pmckeown.dependencytrack.upload;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_BOM_TOKEN_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.upload.BomProcessingResponseBuilder.aBomProcessingResponse;
import static io.github.pmckeown.dependencytrack.upload.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.LinkedHashSet;
import java.util.Set;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class BomClientIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    @Mock
    private Logger logger;

    private BomClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmri) {
        client = new BomClient(getCommonConfig(wmri), logger);
    }

    @Test
    void thatBomCanBeUploadedAndProcessingTokenIsReceived() throws Exception {
        stubFor(post(urlEqualTo(V1_BOM))
                .willReturn(ok().withBody(
                                asJson(anUploadBomResponse().withToken("123").build()))));

        Response<UploadBomResponse> response = client.uploadBom(aBom(), false);

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));
        assertThat(response.getBody().get().getToken(), is(equalTo("123")));

        verify(
                1,
                postRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBodyPart(aMultipart("projectName")
                                .withBody(WireMock.equalTo(PROJECT_NAME))
                                .build())
                        .withRequestBodyPart(aMultipart("projectTags")
                                .withBody(WireMock.equalTo("foo,bar"))
                                .build())
                        .withRequestBodyPart(aMultipart("isLatest")
                                .withBody(WireMock.equalTo("true"))
                                .build())
                        .withRequestBodyPart(aMultipart("bom")
                                .withHeader("Content-Disposition", containing("filename=\"bom.xml\""))
                                .withBody(WireMock.equalTo("blah"))
                                .build()));
    }

    @Test
    void thatBomCanBeUploadedAndProcessingTokenIsReceivedWithPut() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM))
                .willReturn(ok().withBody(
                                asJson(anUploadBomResponse().withToken("123").build()))));

        Response<UploadBomResponse> response = client.uploadBom(aBom(), true);

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));
        assertThat(response.getBody().get().getToken(), is(equalTo("123")));

        verify(
                1,
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME)))
                        .withRequestBody(matchingJsonPath("$.isLatestProjectVersion", WireMock.equalTo("true"))));
    }

    @Test
    void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponse() {
        stubFor(post(urlEqualTo(V1_BOM)).willReturn(status(418)));

        Response<UploadBomResponse> response = client.uploadBom(aBom(), false);

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(
                1,
                postRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBodyPart(aMultipart("projectName")
                                .withBody(WireMock.equalTo(PROJECT_NAME))
                                .build()));
    }

    @Test
    void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponseWithPut() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(status(418)));

        Response<UploadBomResponse> response = client.uploadBom(aBom(), true);

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(
                1,
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponse() {
        stubFor(post(urlEqualTo(V1_BOM)).willReturn(notFound()));

        Response<UploadBomResponse> response = client.uploadBom(aBom(), false);

        assertThat(response.getStatus(), is(equalTo(404)));

        verify(
                1,
                postRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBodyPart(aMultipart("projectName")
                                .withBody(WireMock.equalTo(PROJECT_NAME))
                                .build()));
    }

    @Test
    void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponseWithPut() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(notFound()));

        Response<UploadBomResponse> response = client.uploadBom(aBom(), true);

        assertThat(response.getStatus(), is(equalTo(404)));

        verify(
                1,
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    void thatWhenBomIsStillBeingProcessedThenTheProcessingFlagIsTrue() throws Exception {
        stubFor(get(urlPathMatching(V1_BOM_TOKEN_UUID))
                .willReturn(ok().withBody(asJson(
                        aBomProcessingResponse().withProcessing(true).build()))));

        Response<BomProcessingResponse> response = client.isBomBeingProcessed("123");

        assertThat(response.getBody().get().isProcessing(), is(equalTo(true)));
    }

    @Test
    void thatWhenBomProcessingIsFinishedThenTheProcessingFlagIsFalse() throws Exception {
        stubFor(get(urlPathMatching(V1_BOM_TOKEN_UUID))
                .willReturn(ok().withBody(asJson(
                        aBomProcessingResponse().withProcessing(false).build()))));

        Response<BomProcessingResponse> response = client.isBomBeingProcessed("123");

        assertThat(response.getBody().get().isProcessing(), is(equalTo(false)));
    }

    @Test
    void thatWhenAnErrorOccursWhileQueryingTheAUnirestExceptionIsThrown() {
        stubFor(get(urlPathMatching(V1_BOM_TOKEN_UUID))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

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
        ModuleConfig config = getModuleConfig();
        Set<String> tags = new LinkedHashSet<>();
        tags.add("foo");
        tags.add("bar");
        config.setProjectTags(tags);
        config.setLatest(true);
        return new UploadBomRequest(config, new BomReference("blah"));
    }
}
