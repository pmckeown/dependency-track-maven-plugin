package io.github.pmckeown.dependencytrack.upload;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_BOM;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.TestResourceConstants;
import java.util.HashSet;
import java.util.Set;
import kong.unirest.Unirest;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadBomMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    static final String BOM_LOCATION = TEST_PROJECT + "/bom.xml";

    UploadBomMojo uploadBomMojo;

    @BeforeEach
    @Basedir(TEST_PROJECT)
    @InjectMojo(goal = "upload-bom")
    @MojoParameter(name = "bomLocation", value = BOM_LOCATION)
    @MojoParameter(name = "uploadWithPut", value = "true")
    void setUp(UploadBomMojo mojo) {
        uploadBomMojo = mojo;
        configureMojo(uploadBomMojo);
        uploadBomMojo.setPollingConfig(PollingConfig.disabled());

        // testing-harness 3.4.0 does not resolve default value expessions based on the provided pom
        uploadBomMojo.setProjectName("dependency-track-maven-plugin-test-project");
        uploadBomMojo.setProjectVersion("0.0.1-SNAPSHOT");
    }

    @AfterEach
    void tearDown() {
        uploadBomMojo.getUnirestConfiguration().set(false);
    }

    @Test
    void thatBomCanBeUploadedSuccessfully() throws Exception {
        stubFor(post(urlEqualTo(V1_BOM)).willReturn(ok()));

        // default test config uses PUT instead of the default Mojo config of POST
        uploadBomMojo.setUploadWithPut(false);
        uploadBomMojo.execute();

        verify(exactly(1), postRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    void thatBomCanBeUploadedSuccessfullyWithPut() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    void thatWhenFailOnErrorIsFalseAFailureFromToDependencyTrackDoesNotFailTheBuild() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(notFound()));

        assertDoesNotThrow(
                () -> {
                    uploadBomMojo.setFailOnError(false);
                    uploadBomMojo.execute();
                },
                "No exception expected");

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    void thatWhenFailOnErrorIsTrueAFailureFromToDependencyTrackDoesFailTheBuild() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(notFound()));

        assertDoesNotThrow(
                () -> {
                    uploadBomMojo.setDependencyTrackBaseUrl("http://localghost:80");
                    uploadBomMojo.setFailOnError(true);
                },
                "Exception not expected yet");

        try {
            uploadBomMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    void thatWhenFailOnErrorIsFalseAFailureToConnectToDependencyTrackDoesNotFailTheBuild() {
        // No Wiremock Stubbing

        assertDoesNotThrow(
                () -> {
                    uploadBomMojo.setDependencyTrackBaseUrl("http://localghost:80");
                    uploadBomMojo.setFailOnError(false);
                    uploadBomMojo.execute();
                },
                "No exception expected");
    }

    @Test
    void thatWhenFailOnErrorIsTrueAFailureToConnectToDependencyTrackDoesFailTheBuild() throws Exception {
        // No Wiremock Stubbing

        assertDoesNotThrow(
                () -> {
                    uploadBomMojo.setDependencyTrackBaseUrl("http://localghost:80");
                    uploadBomMojo.setFailOnError(true);
                },
                "Exception not expected yet");

        try {
            uploadBomMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    void thatProjectNameCanBeProvided() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.setProjectName("test-project");
        uploadBomMojo.execute();

        verify(
                exactly(1),
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.projectName", equalTo("test-project"))));
    }

    @Test
    void thatProjectNameDefaultsToArtifactId() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.execute();

        verify(
                exactly(1),
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath(
                                "$.projectName", equalTo("dependency-track-maven-plugin-test-project"))));
    }

    @Test
    void thatProjectVersionCanBeProvided() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.setProjectVersion("99.99.99-RELEASE");
        uploadBomMojo.execute();

        verify(
                exactly(1),
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.projectVersion", equalTo("99.99.99-RELEASE"))));
    }

    @Test
    void thatProjectIsLatestCanBeProvided() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.setLatest(true);
        uploadBomMojo.execute();

        verify(
                exactly(1),
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.isLatestProjectVersion", equalTo("true"))));
    }

    @Test
    void thatProjectTagsCanBeProvided() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.setLatest(true);
        Set<String> tags = new HashSet<>();
        tags.add("Backend");
        tags.add("Team-1");
        uploadBomMojo.setProjectTags(tags);
        uploadBomMojo.execute();

        verify(
                exactly(1),
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath(
                                "$.projectTags", equalToJson("[{\"name\":\"Backend\"},{\"name\":\"Team-1\"}]"))));
    }

    @Test
    void thatProjectVersionDefaultsToPomVersion() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.execute();

        verify(
                exactly(1),
                putRequestedFor(urlEqualTo(V1_BOM))
                        .withRequestBody(matchingJsonPath("$.projectVersion", equalTo("0.0.1-SNAPSHOT"))));
    }

    @Test
    void thatTheUploadIsSkippedWhenSkipIsTrue() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo.setSkip("true");

        uploadBomMojo.execute();

        verify(exactly(0), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    void thatSslVerifyDefaultsToTrue() throws Exception {
        uploadBomMojo.setSkip("true");
        uploadBomMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(true));
    }

    @Test
    void thatProjectParentNameAndVersionCanBeIgnored() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/test-project.json")));
        stubFor(get(urlPathMatching(TestResourceConstants.V1_PROJECT_UUID)).willReturn(ok()));
        stubFor(patch(urlPathMatching(TestResourceConstants.V1_PROJECT_UUID)).willReturn(ok()));
        stubFor(get(urlPathMatching(TestResourceConstants.V1_BOM_TOKEN_UUID)).willReturn(ok()));
        stubFor(put(urlEqualTo(V1_BOM))
                .willReturn(aResponse().withBodyFile("api/v1/project/upload-bom-response.json")));
        stubFor(get(urlPathMatching(TestResourceConstants.V1_METRICS_PROJECT_REFRESH))
                .willReturn(ok()));

        uploadBomMojo.setProjectName("test-project");
        uploadBomMojo.setParentName("test-parent-missing");
        uploadBomMojo.setParentVersion("1.0.0-MISSING");
        uploadBomMojo.setFailOnError(true);
        uploadBomMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
    }
}
