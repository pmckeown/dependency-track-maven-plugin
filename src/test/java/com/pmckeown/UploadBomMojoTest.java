package com.pmckeown;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.pmckeown.rest.ResourceConstants.V1_BOM;
import static org.junit.Assert.*;

public class UploadBomMojoTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(ok()));

        uploadBomMojo().execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    @Test
    public void thatFailureToUploadDoesNotError() throws Exception {
        stubFor(put(urlEqualTo(V1_BOM)).willReturn(status(404)));

        try {
            uploadBomMojo().execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_BOM)));
    }

    /*
     * Helper methods
     */

    private UploadBomMojo uploadBomMojo() throws Exception {
        UploadBomMojo uploadBomMojo = (UploadBomMojo) mojoRule.lookupConfiguredMojo( getPomFile(), "upload-bom" );
        uploadBomMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        assertNotNull(uploadBomMojo);
        return uploadBomMojo;
    }

    private File getPomFile() {
        File pom = new File( "target/test-classes/project-to-test/" );
        assertNotNull( pom );
        assertTrue( pom.exists() );
        return pom;
    }
}

