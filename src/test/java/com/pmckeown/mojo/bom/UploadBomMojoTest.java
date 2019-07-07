package com.pmckeown.mojo.bom;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.pmckeown.util.BomEncoder;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.pmckeown.rest.ResourceConstants.V1_BOM;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class UploadBomMojoTest {

    private static final String TEST_RESOURCES = "target/test-classes/project-to-test/";
    private static final String BOM_LOCATION = TEST_RESOURCES + "bom.xml";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Mock
    private BomEncoder bomEncoder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION);
    }

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo().execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    public void thatFailureToUploadDoesNotError() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(status(404)));

        try {
            uploadBomMojo().execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    public void thatProjectNameCanBeProvided() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo();
        uploadBomMojo.setProjectName("test-project");
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("test-project"))));
    }

    @Test
    public void thatProjectNameDefaultsToArtifactId() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo();
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("dependency-track-maven-plugin-test-project"))));
    }

    @Test
    public void thatProjectVersionCanBeProvided() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo();
        uploadBomMojo.setProjectVersion("99.99.99-RELEASE");
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("99.99.99-RELEASE"))));
    }

    @Test
    public void thatProjectVersionDefaultsToArtifactId() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo();
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("0.0.1-SNAPSHOT"))));
    }

    @Test
    public void thatBomLocationDefaultsToTargetDirectory() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojoWithoutLocation();
        uploadBomMojo.execute();

        // No BOM exists at the default location for this project
        verify(exactly(0), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    /*
     * Helper methods
     */

    private UploadBomMojo uploadBomMojo() throws Exception {
        return uploadBomMojo(BOM_LOCATION);
    }

    private UploadBomMojo uploadBomMojoWithoutLocation() throws Exception {
        return uploadBomMojo(null);
    }

    private UploadBomMojo uploadBomMojo(String location) throws Exception {
        UploadBomMojo uploadBomMojo = (UploadBomMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "upload-bom");
        uploadBomMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        if (location != null) {
            uploadBomMojo.setBomLocation(location);
        }
        uploadBomMojo.setApiKey("ABC123");
        uploadBomMojo.setBomEncoder(bomEncoder);
        assertNotNull(uploadBomMojo);
        return uploadBomMojo;
    }

    private File getPomFile() {
        File pom = new File(TEST_RESOURCES);
        assertNotNull(pom);
        assertTrue(pom.exists());
        return pom;
    }
}

