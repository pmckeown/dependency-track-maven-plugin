package io.github.pmckeown;

import io.github.pmckeown.mojo.bom.UploadBomMojo;
import io.github.pmckeown.mojo.score.ScoreMojo;
import org.apache.maven.plugin.testing.MojoRule;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Helper class to load Mojos
 *
 * @author Paul McKeown
 */
public class TestMojoLoader {

    private static final String TEST_RESOURCES = "target/test-classes/project-to-test/";

    public static UploadBomMojo loadUploadBomMojo(MojoRule mojoRule) throws Exception {
        UploadBomMojo uploadBomMojo = (UploadBomMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "upload-bom");
        assertNotNull(uploadBomMojo);
        return uploadBomMojo;
    }

    public static ScoreMojo loadScoreMojo(MojoRule mojoRule) throws Exception {
        ScoreMojo scoreMojo = (ScoreMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "score");
        assertNotNull(scoreMojo);
        return scoreMojo;
    }

    private static File getPomFile() {
        File pom = new File(TEST_RESOURCES);
        assertNotNull(pom);
        assertTrue(pom.exists());
        return pom;
    }
}
