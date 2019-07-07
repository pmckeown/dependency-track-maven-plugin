package io.github.pmckeown.mojo.bom;


import io.github.pmckeown.TestMojoLoader;
import io.github.pmckeown.mojo.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.util.BomEncoder;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.rest.ResourceConstants.V1_BOM;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

/**
 * Test upload configuration options
 *
 * @author Paul McKeown
 */
public class UploadBomConfigurationMojoTest extends AbstractDependencyTrackMojoTest {

    private static final String BOM_LOCATION = "target/test-classes/project-to-test/bom.xml";

    @Mock
    private BomEncoder bomEncoder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION);
    }

    @Test
    public void thatProjectNameCanBeProvided() throws Exception {
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
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("dependency-track-maven-plugin-test-project"))));
    }

    @Test
    public void thatProjectVersionCanBeProvided() throws Exception {
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
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("0.0.1-SNAPSHOT"))));
    }

    @Test
    public void thatBomLocationDefaultsToTargetDirectory() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(null);
        uploadBomMojo.execute();

        // No BOM exists at the default location for this project
        verify(exactly(0), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    public void thatUploadFailureCanFailBuild() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(badRequest()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        uploadBomMojo.setFailOnError(true);
        try {
            uploadBomMojo.execute();
            fail("Exception expected");
        } catch (MojoFailureException ex) {
            assertNotNull(ex);
        }
    }

    @Test
    public void thatBuildFailureOnUploadFailureDefaultsToFalse() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(badRequest()));

        UploadBomMojo uploadBomMojo = uploadBomMojo(BOM_LOCATION);
        try {
            uploadBomMojo.execute();
        } catch (MojoFailureException ex) {
            fail("Exception not expected");
        }
    }

    /*
     * Helper methods
     */

    private UploadBomMojo uploadBomMojo(String bomLocation) throws Exception {
        UploadBomMojo uploadBomMojo = TestMojoLoader.loadUploadBomMojo(mojoRule);
        uploadBomMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        if (bomLocation != null) {
            uploadBomMojo.setBomLocation(bomLocation);
        }
        uploadBomMojo.setApiKey("ABC123");
        uploadBomMojo.setBomEncoder(bomEncoder);
        return uploadBomMojo;
    }
}

