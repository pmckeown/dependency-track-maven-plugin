package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import kong.unirest.Unirest;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.TestMojoLoader.loadUploadBomMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class UploadBomMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    private static final String BOM_LOCATION = "target/test-classes/projects/run/bom.xml";

    @Mock
    private BomEncoder bomEncoder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(anyString(), any(Logger.class));
    }

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        uploadBomMojo(BOM_LOCATION).execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM)));
    }

    @Test
    public void thatWhenFailOnErrorIsFalseAFailureFromToDependencyTrackDoesNotFailTheBuild() {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(notFound()));

        try {
            UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
            uploadBomMojo.setFailOnError(false);
            uploadBomMojo.execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }

        verify(exactly(1), putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM)));
    }

    @Test
    public void thatWhenFailOnErrorIsTrueAFailureFromToDependencyTrackDoesFailTheBuild() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(notFound()));

        UploadBomMojo uploadBomMojo = null;
        try {
            uploadBomMojo = uploadBomMojo(BOM_LOCATION);
            uploadBomMojo.setDependencyTrackBaseUrl("http://localghost:80");
            uploadBomMojo.setFailOnError(true);
        } catch (Exception ex) {
            fail("Exception not expected yet");
        }

        try {
            uploadBomMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    public void thatWhenFailOnErrorIsFalseAFailureToConnectToDependencyTrackDoesNotFailTheBuild() {
        // No Wiremock Stubbing

        try {
            UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
            uploadBomMojo.setDependencyTrackBaseUrl("http://localghost:80");
            uploadBomMojo.setFailOnError(false);
            uploadBomMojo.execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatWhenFailOnErrorIsTrueAFailureToConnectToDependencyTrackDoesFailTheBuild() throws Exception {
        // No Wiremock Stubbing

        UploadBomMojo uploadBomMojo = null;

        try {
            uploadBomMojo = uploadBomMojo(BOM_LOCATION);
            uploadBomMojo.setDependencyTrackBaseUrl("http://localghost:80");
            uploadBomMojo.setFailOnError(true);
        } catch (Exception ex) {
            fail("Exception not expected yet");
        }

        try {
            uploadBomMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    public void thatProjectNameCanBeProvided() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.setProjectName("test-project");
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("test-project"))));
    }

    @Test
    public void thatProjectNameDefaultsToArtifactId() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("dependency-track-maven-plugin-test-project"))));
    }

    @Test
    public void thatProjectVersionCanBeProvided() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));


        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.setProjectVersion("99.99.99-RELEASE");
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("99.99.99-RELEASE"))));
    }

    @Test
    public void thatProjectVersionDefaultsToPomVersion() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("0.0.1-SNAPSHOT"))));
    }

    @Test
    public void thatTheUploadIsSkippedWhenSkipIsTrue() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo("target/test-classes/projects/skip/bom.xml");
        uploadBomMojo.setSkip(true);

        uploadBomMojo.execute();

        verify(exactly(0), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    public void thatSslVerifyDefaultsToTrue() throws Exception {
        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.setSkip(true);
        uploadBomMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(true));
    }

    /*
     * Helper methods
     */

    private UploadBomMojo uploadBomMojo(String bomLocation) throws Exception {
        UploadBomMojo uploadBomMojo = loadUploadBomMojo(mojoRule);
        uploadBomMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        if (bomLocation != null) {
            uploadBomMojo.setBomLocation(bomLocation);
        }
        uploadBomMojo.setApiKey("ABC123");
        return uploadBomMojo;
    }
}
