package com.pmckeown.mojo.bom;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.pmckeown.TestMojoLoader;
import com.pmckeown.util.BomEncoder;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.pmckeown.rest.ResourceConstants.V1_BOM;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

/**
 * Test bom uploading
 *
 * @author Paul McKeown
 */
public class UploadBomMojoTest {

    private static final String BOM_LOCATION = "target/test-classes/project-to-test/bom.xml";

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

        uploadBomMojo(BOM_LOCATION).execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    public void thatFailureToUploadDoesNotError() {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(status(404)));

        try {
            uploadBomMojo(BOM_LOCATION).execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
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

